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

    SkipOp(Op<IN> op, long size) {
        super(op);
        this.size = size;
    }

    @Override
    public void accept(IN in) {
        if (skipped < size) {
            skipped++;
        } else {
            next.accept(in);
        }
    }
}
