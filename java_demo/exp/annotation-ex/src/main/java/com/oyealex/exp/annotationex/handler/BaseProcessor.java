package com.oyealex.exp.annotationex.handler;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;

/**
 * 基础处理器
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-30
 */
public abstract class BaseProcessor extends AbstractProcessor {
    /** 消息记录器 */
    protected Messager messager;

    /** 可将Element转换为JCTree的工具 */
    protected JavacTrees trees;

    /** JCTree制作器 */
    protected TreeMaker treeMaker;

    /** 名字处理器 */
    protected Names names;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        ProcessingEnvironment unwrappedEnv = unwrapJetbrains(ProcessingEnvironment.class, env);
        super.init(unwrappedEnv);
        this.messager = unwrappedEnv.getMessager();
        this.trees = JavacTrees.instance(unwrappedEnv);
        Context context = ((JavacProcessingEnvironment) unwrappedEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    private static <T> T unwrapJetbrains(Class<? extends T> type, T wrapper) {
        // for jetbrains issue
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader()
                    .loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = type.cast(unwrapMethod.invoke(null, type, wrapper));
        } catch (Throwable ignored) {}
        return unwrapped != null ? unwrapped : wrapper;
    }

    protected void noteLog(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    protected void warnLog(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    protected void errLog(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }
}
