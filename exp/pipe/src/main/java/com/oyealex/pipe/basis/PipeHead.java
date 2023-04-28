package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.op.Op;

import java.util.Iterator;

/**
 * 流水线头节点，提供将迭代器中的数据泵入流水线的能力，以及一些形如管理关闭动作等的能力。
 *
 * @param <OUT> 泵入流水线的元素类型
 * @author oyealex
 * @since 2023-04-29
 */
class PipeHead<OUT> extends ReferencePipe<Void, OUT> {
    /** 流水线数据源 */
    private Iterator<? extends OUT> sourceIterator;

    /** 流水线关闭时执行的动作 */
    private Runnable closeAction;

    public PipeHead(Iterator<? extends OUT> sourceIterator) {
        this.sourceIterator = sourceIterator;
    }

    @Override
    protected Iterator<?> getDataSource() {
        Iterator<? extends OUT> iterator = sourceIterator;
        sourceIterator = null;
        return iterator;
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
    protected Op<Void> wrapOp(Op<OUT> op) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pipe<OUT> onClose(Runnable closeAction) {
        if (this.closeAction == null) {
            this.closeAction = closeAction;
        } else {
            this.closeAction = Misc.composeAction(this.closeAction, closeAction);
        }
        return this;
    }
}
