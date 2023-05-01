package com.oyealex.pipe.basis.op;

import java.util.Objects;

/**
 * 支持链接的操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
public abstract class ChainedOp<IN, OUT> implements Op<IN> {
    /** 下一个操作 */
    protected final Op<? super OUT> next;

    public ChainedOp(Op<? super OUT> next) {
        this.next = Objects.requireNonNull(next);
    }

    @Override
    public void begin(long size) {
        next.begin(size);
    }

    @Override
    public void end() {
        next.end();
    }

    @Override
    public boolean cancellationRequested() {
        return next.cancellationRequested();
    }
}
