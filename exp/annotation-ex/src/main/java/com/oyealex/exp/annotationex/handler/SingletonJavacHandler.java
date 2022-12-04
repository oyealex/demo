package com.oyealex.exp.annotationex.handler;

import com.oyealex.exp.annotationex.annotations.Singleton;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.sun.tools.javac.code.Flags.PRIVATE;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Flags.STATIC;
import static com.sun.tools.javac.util.List.nil;

/**
 * {@link Singleton}单例注解处理器
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-25
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.oyealex.exp.annotationex.annotations.Singleton")
@MetaInfServices(Processor.class)
public class SingletonJavacHandler extends BaseProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Singleton.class);
        elements.forEach(this::handleAnnotation);
        return false;
    }

    private void handleAnnotation(Element element) {
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

    private Name generateHolderClassName(String name) {
        return names.fromString("$" + name + "$SingletonHolder");
    }
}
