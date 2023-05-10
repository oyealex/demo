package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.flag.PipeFlag;

import java.util.function.Predicate;

/**
 * KeepOrDropWhileStage
 *
 * @author oyealex
 * @since 2023-05-10
 */
abstract class WhileStage<T> extends RefPipe<T, T> {
    protected WhileStage(RefPipe<?, ? extends T> prePipe) {
        super(prePipe, PipeFlag.NOT_SIZED);
    }

    static class KeepWhile<T> extends WhileStage<T> {
        private final Predicate<? super T> predicate;

        KeepWhile(RefPipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new WhileOp<>(nextOp, true) {
                @Override
                public void accept(T var) {
                    if (shouldTake && (shouldTake = predicate.test(var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class DropWhile<T> extends WhileStage<T> {
        private final Predicate<? super T> predicate;

        DropWhile(RefPipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new WhileOp<>(nextOp, false) {
                @Override
                public void accept(T var) {
                    if (shouldTake || (shouldTake = !predicate.test(var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class KeepWhileOrderly<T> extends WhileStage<T> {
        private final LongBiPredicate<? super T> predicate;

        KeepWhileOrderly(RefPipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new WhileOp<>(nextOp, true) {
                private long index = 0L;

                @Override
                public void accept(T var) {
                    if (shouldTake && (shouldTake = predicate.test(index++, var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class DropWhileOrderly<T> extends WhileStage<T> {
        private final LongBiPredicate<? super T> predicate;

        DropWhileOrderly(RefPipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new WhileOp<>(nextOp, false) {
                private long index = 0L;

                @Override
                public void accept(T var) {
                    if (shouldTake || (shouldTake = !predicate.test(index++, var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    private abstract static class WhileOp<T> extends ChainedOp<T, T> {
        protected boolean shouldTake;

        protected WhileOp(Op<? super T> nextOp, boolean shouldTake) {
            super(nextOp);
            this.shouldTake = shouldTake;
        }
    }
}
