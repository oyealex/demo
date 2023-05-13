package com.oyealex.pipe.basis;

import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.NOTHING;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * LimitOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class SliceOp<IN> extends RefPipe<IN, IN> {
    private final long skip;

    private final long limit;

    SliceOp(RefPipe<?, ? extends IN> prePipe, long skip, long limit) {
        super(prePipe, NOT_SIZED | (limit != Long.MAX_VALUE ? IS_SHORT_CIRCUIT : NOTHING));
        this.skip = skip;
        this.limit = limit;
    }

    @Override
    protected Op<IN> wrapOp(Op<IN> nextOp) {
        return new ChainedOp<>(nextOp) {
            private long skipped = 0L;

            private long limited = 0L;

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
        };
    }
}
