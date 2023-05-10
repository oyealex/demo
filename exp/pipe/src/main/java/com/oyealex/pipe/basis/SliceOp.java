package com.oyealex.pipe.basis;

/**
 * LimitOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class SliceOp<IN> extends ChainedOp<IN, IN> {
    private final long skip;

    private final long limit;

    private long skipped = 0L;

    private long limited = 0L;

    SliceOp(Op<? super IN> nextOp, long skip, long limit) {
        super(nextOp);
        this.skip = skip;
        this.limit = limit;
    }

    @Override
    public void accept(IN in) {
        if (skipped < skip) {
            skipped++;
        } else if (limited < limit) {
            limited++;
            nextOp.accept(in);
        }
    }

    @Override
    public boolean canShortCircuit() {
        return limited >= limit || nextOp.canShortCircuit();
    }
}
