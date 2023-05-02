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
    protected final Op<? super OUT> nextOp;

    public ChainedOp(Op<? super OUT> nextOp) {
        this.nextOp = Objects.requireNonNull(nextOp);
    }

    @Override
    public void begin(long size) {
        nextOp.begin(size);
    }

    @Override
    public void end() {
        nextOp.end();
    }

    @Override
    public boolean cancellationRequested() {
        return nextOp.cancellationRequested();
    }
}
