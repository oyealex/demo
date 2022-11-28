package com.oyealex.exp.annotationex.handler;

import com.oyealex.exp.annotationex.annotations.Singleton;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Method;
import java.util.Set;

import static com.sun.tools.javac.code.Flags.PRIVATE;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Flags.STATIC;
import static com.sun.tools.javac.util.List.nil;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * SingleTonJavacHandler
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-25
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.oyealex.exp.annotationex.annotations.Singleton")
@MetaInfServices(Processor.class)
public class SingletonJavacHandler extends AbstractProcessor {
    /** 消息记录器 */
    private Messager messager;

    /** 可将Element转换为JCTree的工具 */
    private JavacTrees trees;

    /** JCTree制作器 */
    private TreeMaker treeMaker;

    /** 名字处理器 */
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        ProcessingEnvironment unwrappedEnv = unwrapJetbrains(ProcessingEnvironment.class, env);
        super.init(unwrappedEnv);
        this.messager = unwrappedEnv.getMessager();
        messager.printMessage(NOTE, "annotation-ex: init");
        this.trees = JavacTrees.instance(unwrappedEnv);
        Context context = ((JavacProcessingEnvironment) unwrappedEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    private static <T> T unwrapJetbrains(Class<? extends T> type, T wrapper) {
        // for idea issue
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader()
                    .loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = type.cast(unwrapMethod.invoke(null, type, wrapper));
        } catch (Throwable ignored) {}
        return unwrapped != null ? unwrapped : wrapper;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        messager.printMessage(NOTE, "annotation-ex: start process");
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Singleton.class);
        elements.forEach(this::handleAnnotation);
        return false;
    }

    private void handleAnnotation(Element element) {
        messager.printMessage(NOTE, "annotation-ex: processing " + element);
        if (element.getKind() != ElementKind.CLASS) {
            return;
        }
        trees.getTree(element).accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl classDecl) {
                super.visitClassDef(classDecl);
                JCTree.JCClassDecl holderClass = generateHolderClass(classDecl.sym);
                JCTree.JCMethodDecl getterMethod = generateSingletonGetter(classDecl.sym, holderClass);
                classDecl.defs = classDecl.defs.append(getterMethod);
                classDecl.defs = classDecl.defs.append(holderClass);
            }
        });
    }

    private JCTree.JCMethodDecl generateSingletonGetter(Symbol.ClassSymbol classSymbol,
            JCTree.JCClassDecl holderClassDecl) {
        // @formatter:off
        return treeMaker.MethodDef(
                treeMaker.Modifiers(PUBLIC + STATIC),
                names.fromString("getSingleton"),
                treeMaker.Ident(classSymbol.name),
                nil(),
                nil(),
                nil(),
                generateReturnSingletonBlock(holderClassDecl),
                null);
        // @formatter:on
    }

    private JCTree.JCBlock generateReturnSingletonBlock(JCTree.JCClassDecl holderClassDecl) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(
                treeMaker.Select(treeMaker.Ident(holderClassDecl.name), names.fromString("SINGLETON"))));
        return treeMaker.Block(0L, statements.toList());
    }

    private JCTree.JCClassDecl generateHolderClass(Symbol.ClassSymbol classSymbol) {
        // @formatter:off
        JCTree.JCClassDecl classDecl = treeMaker.ClassDef(
                treeMaker.Modifiers(PRIVATE + STATIC),
                generateHolderClassName(classSymbol.getSimpleName().toString()),
                nil(),
                null,
                nil(),
                nil());
        JCTree.JCVariableDecl instanceVarExp = treeMaker.VarDef(
                treeMaker.Modifiers(PRIVATE + Flags.FINAL + STATIC),
                names.fromString("SINGLETON"),
                treeMaker.Ident(classSymbol.name),
                // generateTypeExp(symbol.getQualifiedName().toString()),
                treeMaker.NewClass(null, nil(), treeMaker.Ident(classSymbol.name), nil(), null));
        // @formatter:on
        classDecl.defs = classDecl.defs.append(instanceVarExp);
        return classDecl;
    }

    private JCTree.JCExpression generateTypeExp(String typeFullName) {
        String[] typeNameParts = typeFullName.split("\\.");
        JCTree.JCExpression typeExp = treeMaker.Ident(names.fromString(typeNameParts[0]));
        for (int i = 1; i < typeNameParts.length; i++) {
            typeExp = treeMaker.Select(typeExp, names.fromString(typeNameParts[i]));
        }
        return typeExp;
    }

    private Name generateHolderClassName(String name) {
        return names.fromString("$" + name + "$SingletonHolder");
    }
}
