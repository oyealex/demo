package com.oyealex.pipe.basis.op;

import java.util.function.Predicate;

/**
 * 过滤操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
abstract class KeepOrDropWhileOps<IN> extends ChainedOp<IN, IN> {
    protected final Predicate<? super IN> predicate;

    protected boolean shouldTake;

    KeepOrDropWhileOps(Op<IN> nextOp, Predicate<? super IN> predicate, boolean shouldTakeInit) {
        super(nextOp);
        this.predicate = predicate;
        this.shouldTake = shouldTakeInit;
    }

    static class KeepWhileOp<IN> extends KeepOrDropWhileOps<IN> {
        KeepWhileOp(Op<IN> nextOp, Predicate<? super IN> predicate) {
            super(nextOp, predicate, true);
        }

        @Override
        public void accept(IN in) {
            if (shouldTake && (shouldTake = predicate.test(in))) {
                nextOp.accept(in);
            }
        }
    }

    static class DropWhileOp<IN> extends KeepOrDropWhileOps<IN> {
        DropWhileOp(Op<IN> nextOp, Predicate<? super IN> predicate) {
            super(nextOp, predicate, false);
        }

        @Override
        public void accept(IN in) {
            if (shouldTake || (shouldTake = !predicate.test(in))) {
                nextOp.accept(in);
            }
        }
    }
}
