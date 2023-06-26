package com.oyealex.pipe.basis;

import com.oyealex.pipe.flag.PipeFlag;

import java.util.Spliterator;

/**
 * 流水线头节点，提供将迭代器中的数据泵入流水线的能力，以及一些形如管理关闭动作等的能力。
 *
 * @param <OUT> 泵入流水线的元素类型
 * @author oyealex
 * @since 2023-04-29
 */
class PipeHead<OUT> extends RefPipe<Void, OUT> {
    /** 流水线数据源 */
    private Spliterator<? extends OUT> sourceSpliterator;

    /** 流水线关闭时执行的动作 */
    private Runnable closeAction;

    PipeHead(Spliterator<? extends OUT> sourceSpliterator) {
        super(PipeFlag.fromSpliterator(sourceSpliterator));
        this.sourceSpliterator = sourceSpliterator;
    }

    PipeHead(Spliterator<? extends OUT> sourceSpliterator, int extraFlag) { // OPT 2023-05-26 00:54 对额外标记进行校验
        super(PipeFlag.fromSpliterator(sourceSpliterator) | extraFlag);
        this.sourceSpliterator = sourceSpliterator;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Spliterator<Object> takeDataSource() {
        Spliterator<? extends OUT> iterator = sourceSpliterator;
        sourceSpliterator = null;
        return (Spliterator<Object>) iterator;
    }

    @Override
    public void close() {
        Runnable action = closeAction;
        if (action != null) {
            closeAction = null;
            action.run();
        }
    }

    @Override
    protected Op<Void> wrapOp(Op<OUT> nextOp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pipe<OUT> onClose(Runnable closeAction) {
        if (this.closeAction == null) {
            this.closeAction = closeAction;
        } else {
            this.closeAction = composeAction(this.closeAction, closeAction);
        }
        return this;
    }

    private static Runnable composeAction(Runnable action, Runnable anotherAction) {
        return () -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                try {
                    anotherAction.run();
                } catch (Throwable anotherThrowable) {
                    try {
                        throwable.addSuppressed(anotherThrowable);
                    } catch (Throwable ignore) {
                        // noop
                    }
                }
                throw throwable;
            }
            anotherAction.run();
        };
    }
}
