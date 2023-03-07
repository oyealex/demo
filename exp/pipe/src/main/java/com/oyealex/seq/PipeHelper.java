package com.oyealex.seq;

import java.util.Objects;

/**
 * 抽象流水线节点
 *
 * @author oyealex
 * @since 2023-03-04
 */
abstract class PipeHelper<P extends BasePipe<P>> implements BasePipe<P> {
    /** 流水线的源节点，不会为null */
    private final PipeHelper<?> sourceNode;

    /** 此节点的前置节点，当且仅当此节点为源节点时为null */
    private final PipeHelper<?> preNode;

    /** 此节点的后置节点，当且仅当此节点为末端节点时为null */
    private PipeHelper<?> nextNode;

    /** 流水线关闭时执行的动作 */
    private Runnable closeAction;

    PipeHelper() {
        this.sourceNode = this;
        this.preNode = null;
    }

    PipeHelper(PipeHelper<?> preNode) {
        this.sourceNode = preNode.sourceNode;
        this.preNode = preNode;
        preNode.nextNode = this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P onClose(Runnable closeAction) {
        Objects.requireNonNull(closeAction);
        if (sourceNode.closeAction == null) {
            sourceNode.closeAction = closeAction;
        } else {
            sourceNode.closeAction = composeCloseAction(sourceNode.closeAction, closeAction);
        }
        return (P) this;
    }

    @Override
    public void close() {
        Runnable action = sourceNode.closeAction;
        if (action != null) {
            sourceNode.closeAction = null;
            action.run();
        }
    }

    private static Runnable composeCloseAction(Runnable action, Runnable anotherAction) {
        return () -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                try {
                    anotherAction.run();
                } catch (Throwable anotherThrowable) {
                    try {
                        throwable.addSuppressed(anotherThrowable);
                    } catch (Throwable ignore) {}
                }
                throw throwable;
            }
            anotherAction.run();
        };
    }
}
