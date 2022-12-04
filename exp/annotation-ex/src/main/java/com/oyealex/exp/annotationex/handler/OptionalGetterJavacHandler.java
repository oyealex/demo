package com.oyealex.exp.annotationex.handler;

import com.oyealex.exp.annotationex.annotations.OptionalGetter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

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

    private void handle(Element element) {
        noteLog("type: " + element.getKind() + ", " + element);

        // List<JCTree.JCVariableDecl>
        // trees.getTree(element).accept(new TreeTranslator() {
        //     @Override
        //     public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        //         super.visitClassDef(jcClassDecl);
        //     }
        //
        //     @Override
        //     public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
        //         super.visitVarDef(jcVariableDecl);
        //     }
        // });
    }
}
