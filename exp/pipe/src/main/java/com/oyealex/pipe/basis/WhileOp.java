package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiPredicate;

import java.util.function.Predicate;

import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * KeepOrDropWhileStage
 *
 * @author oyealex
 * @since 2023-05-10
 */
abstract class WhileOp<T> extends RefPipe<T, T> {
    protected WhileOp(RefPipe<?, ? extends T> prePipe, int opFlag) {
        super(prePipe, opFlag);
    }

    static class TakeWhile<T> extends com.oyealex.pipe.basis.WhileOp<T> {
        private final Predicate<? super T> predicate;

        TakeWhile(RefPipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe, NOT_SIZED | IS_SHORT_CIRCUIT);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new InternalOp<T>(nextOp, true) {
                @Override
                public void accept(T value) {
                    if (shouldTake && (shouldTake = predicate.test(value))) {
                        nextOp.accept(value);
                    }
                }

                @Override
                public boolean canShortCircuit() {
                    return !shouldTake || nextOp.canShortCircuit();
                }
            };
        }
    }

    static class DropWhile<T> extends com.oyealex.pipe.basis.WhileOp<T> {
        private final Predicate<? super T> predicate;

        DropWhile(RefPipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe, NOT_SIZED);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new InternalOp<T>(nextOp, false) {
                @Override
                public void accept(T value) {
                    if (shouldTake || (shouldTake = !predicate.test(value))) {
                        nextOp.accept(value);
                    }
                }
            };
        }
    }

    static class TakeWhileOrderly<T> extends com.oyealex.pipe.basis.WhileOp<T> {
        private final LongBiPredicate<? super T> predicate;

        TakeWhileOrderly(RefPipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe, NOT_SIZED | IS_SHORT_CIRCUIT);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new InternalOp<T>(nextOp, true) {
                private long index = 0L;

                @Override
                public void accept(T value) {
                    if (shouldTake && (shouldTake = predicate.test(index++, value))) {
                        nextOp.accept(value);
                    }
                }

                @Override
                public boolean canShortCircuit() {
                    return !shouldTake || nextOp.canShortCircuit();
                }
            };
        }
    }

    static class DropWhileOrderly<T> extends com.oyealex.pipe.basis.WhileOp<T> {
        private final LongBiPredicate<? super T> predicate;

        DropWhileOrderly(RefPipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe, NOT_SIZED);
            this.predicate = predicate;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new InternalOp<T>(nextOp, false) {
                private long index = 0L;

                @Override
                public void accept(T value) {
                    if (shouldTake || (shouldTake = !predicate.test(index++, value))) {
                        nextOp.accept(value);
                    }
                }
            };
        }
    }

    private abstract static class InternalOp<T> extends ChainedOp<T, T> {
        protected boolean shouldTake;

        protected InternalOp(Op<? super T> nextOp, boolean shouldTake) {
            super(nextOp);
            this.shouldTake = shouldTake;
        }

        @Override
        public void begin(long size) {
            nextOp.begin(-1);
        }
    }
}
