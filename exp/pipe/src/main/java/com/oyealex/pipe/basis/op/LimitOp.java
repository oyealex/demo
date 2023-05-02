package com.oyealex.pipe.basis.op;

/**
 * LimitOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class LimitOp<IN> extends ChainedOp<IN, IN> {
    private final long size;

    private long limited = 0L;

    LimitOp(Op<IN> nextOp, long size) {
        super(nextOp);
        this.size = size;
    }

    @Override
    public void accept(IN in) {
        if (limited < size) {
            limited++;
            nextOp.accept(in);
        }
    }
}
