package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.LongBiPredicate;

/**
 * 过滤操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
abstract class KeepOrDropWhileEnumeratedOps<IN> extends ChainedOp<IN, IN> {
    protected final LongBiPredicate<? super IN> predicate;

    protected boolean shouldTake;

    protected long index = 0L;

    KeepOrDropWhileEnumeratedOps(Op<IN> nextOp, LongBiPredicate<? super IN> predicate, boolean shouldTakeInit) {
        super(nextOp);
        this.predicate = predicate;
        this.shouldTake = shouldTakeInit;
    }

    static class KeepWhileOp<IN> extends KeepOrDropWhileEnumeratedOps<IN> {
        KeepWhileOp(Op<IN> nextOp, LongBiPredicate<? super IN> predicate) {
            super(nextOp, predicate, true);
        }

        @Override
        public void accept(IN in) {
            if (shouldTake && (shouldTake = predicate.test(index++, in))) {
                nextOp.accept(in);
            }
        }
    }

    static class DropWhileOp<IN> extends KeepOrDropWhileEnumeratedOps<IN> {
        DropWhileOp(Op<IN> nextOp, LongBiPredicate<? super IN> predicate) {
            super(nextOp, predicate, false);
        }

        @Override
        public void accept(IN in) {
            if (shouldTake || (shouldTake = !predicate.test(index++, in))) {
                nextOp.accept(in);
            }
        }
    }
}
