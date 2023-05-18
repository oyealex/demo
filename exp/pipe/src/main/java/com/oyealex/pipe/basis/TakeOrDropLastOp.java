package com.oyealex.pipe.basis;

import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.SIZED;

/**
 * TakeOrDropLastOp
 *
 * @author oyealex
 * @since 2023-05-19
 */
class TakeOrDropLastOp<T> extends RefPipe<T, T> {
    private final boolean isTake;

    private final long count;

    private final boolean isSized;

    TakeOrDropLastOp(RefPipe<?, ? extends T> prePipe, boolean isTake, long count) {
        super(prePipe, NOT_SIZED | (isTake ? EMPTY : IS_SHORT_CIRCUIT));
        this.isTake = isTake;
        this.count = count;
        this.isSized = prePipe.isFlagSet(SIZED);
    }

    @Override
    protected Op<T> wrapOp(Op<T> nextOp) {
        return isSized ? new Sized(nextOp) : new Unsized(nextOp);
    }

    private class Sized extends ChainedOp<T, T> {
        private long countBeforeLast;

        private Sized(Op<? super T> nextOp) {
            super(nextOp);
        }

        @Override
        public void begin(long size) {
            countBeforeLast = Math.max(0, size - count);
            nextOp.begin(isTake ? Math.min(size, count) : Math.max(0, size - count));
        }

        @Override
        public void accept(T value) {
            if (isTake) {
                if (countBeforeLast <= 0) {
                    nextOp.accept(value);
                } else {
                    countBeforeLast--;
                }
            } else if (countBeforeLast-- > 0) {
                nextOp.accept(value);
            }
        }

        @Override
        public boolean canShortCircuit() {
            return (!isTake && countBeforeLast <= 0) || super.canShortCircuit();
        }
    }

    private class Unsized extends ChainedOp<T, T> {
        private Unsized(Op<? super T> nextOp) {
            super(nextOp);
        }

        @Override
        public void begin(long size) {
            super.begin(size);
        }

        @Override
        public void accept(T value) {

        }
    }
}
