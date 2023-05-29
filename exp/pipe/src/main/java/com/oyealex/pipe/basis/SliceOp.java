package com.oyealex.pipe.basis;

import java.util.function.Predicate;

import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
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

    private final Predicate<? super IN> predicate; // TODO 2023-05-30 01:23 断言会导致begin size不同，需要分化处理

    SliceOp(RefPipe<?, ? extends IN> prePipe, long skip, long limit, Predicate<? super IN> predicate) {
        super(prePipe, NOT_SIZED | (limit != Long.MAX_VALUE ? IS_SHORT_CIRCUIT : EMPTY));
        this.skip = skip;
        this.limit = limit;
        this.predicate = predicate;
    }

    @Override
    protected Op<IN> wrapOp(Op<IN> nextOp) {
        return new ChainedOp<IN, IN>(nextOp) {
            private long skipped = 0L;

            private long limited = 0L;

            @Override
            public void begin(long size) {
                if (size <= 0) {
                    nextOp.begin(size);
                } else {
                    nextOp.begin(Math.min(Math.max(0, size - skip), limit));
                }
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
        };
    }
}
