package com.oyealex.exp.annotationex.handler;

import com.oyealex.exp.annotationex.annotations.OptionalGetter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
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

import static com.sun.tools.javac.util.List.nil;

/**
 * {@link OptionalGetter}注解处理器
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-30
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.oyealex.exp.annotationex.annotations.OptionalGetter")
@MetaInfServices(Processor.class)
public class OptionalGetterJavacHandler extends BaseProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        env.getElementsAnnotatedWith(OptionalGetter.class).forEach(this::handle);
        return false;
    }

    private void handle(Element fieldEle) {
        if (fieldEle.getKind() != ElementKind.FIELD) {
            warnLog("non-filed element annotated with OptionalGetter, kind: " + fieldEle.getKind());
            return;
        }
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) trees.getTree(fieldEle);
        Element classEle = fieldEle.getEnclosingElement();
        if (classEle.getKind() != ElementKind.CLASS) {
            warnLog("OptionalGetter annotated element in non-class parent, kind: " + classEle.getKind());
            return;
        }
        JCTree.JCClassDecl clazz = (JCTree.JCClassDecl) trees.getTree(classEle);
        clazz.defs = clazz.defs.append(generateOptionalGetter(field));
    }

    private JCTree.JCMethodDecl generateOptionalGetter(JCTree.JCVariableDecl field) {
        // @formatter:off
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("get" + field.name.toString() + "Opt"),
                field.type != null ? treeMaker.Type(field.type) : field.vartype,
                nil(),
                nil(),
                nil(),
                generateBody(field),
                null);
        // @formatter:on
    }

    private JCTree.JCBlock generateBody(JCTree.JCVariableDecl field) {
        return treeMaker.Block(0, List.of(treeMaker.Return(treeMaker.Ident(field.name))));
    }
}
