package com.oyealex.pipe.basis.op;

/**
 * LimitOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class SkipOp<IN> extends ChainedOp<IN, IN> {
    private final long size;

    private long skipped = 0L;

    SkipOp(Op<IN> nextOp, long size) {
        super(nextOp);
        this.size = size;
    }

    @Override
    public void accept(IN in) {
        if (skipped < size) {
            skipped++;
        } else {
            nextOp.accept(in);
        }
    }
}
