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
abstract class KeepOrDropWhileStage<T> extends ReferencePipe<T, T> {
    protected final Predicate<? super T> predicate;

    protected final LongBiPredicate<? super T> enumeratedPredicate;

    protected boolean shouldTake;

    protected long index = 0L;

    private KeepOrDropWhileStage(ReferencePipe<?, ? extends T> prePipe, Predicate<? super T> predicate,
        LongBiPredicate<? super T> enumeratedPredicate, boolean shouldTakeInit) {
        super(prePipe, PipeFlag.NOT_SIZED);
        this.predicate = predicate;
        this.enumeratedPredicate = enumeratedPredicate;
        this.shouldTake = shouldTakeInit;
    }

    static class KeepWhile<T> extends KeepOrDropWhileStage<T> {
        KeepWhile(ReferencePipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe, predicate, null, true);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp<>(nextOp) {
                @Override
                public void accept(T var) {
                    if (shouldTake && (shouldTake = predicate.test(var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class DropWhile<T> extends KeepOrDropWhileStage<T> {
        DropWhile(ReferencePipe<?, ? extends T> prePipe, Predicate<? super T> predicate) {
            super(prePipe, predicate, null, false);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp<>(nextOp) {
                @Override
                public void accept(T var) {
                    if (shouldTake || (shouldTake = !predicate.test(var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class KeepWhileOrderly<T> extends KeepOrDropWhileStage<T> {
        KeepWhileOrderly(ReferencePipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe, null, predicate, true);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp<>(nextOp) {
                @Override
                public void accept(T var) {
                    if (shouldTake && (shouldTake = enumeratedPredicate.test(index++, var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }

    static class DropWhileOrderly<T> extends KeepOrDropWhileStage<T> {
        DropWhileOrderly(ReferencePipe<?, ? extends T> prePipe, LongBiPredicate<? super T> predicate) {
            super(prePipe, null, predicate, false);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp<>(nextOp) {
                @Override
                public void accept(T var) {
                    if (shouldTake || (shouldTake = !enumeratedPredicate.test(index++, var))) {
                        nextOp.accept(var);
                    }
                }
            };
        }
    }
}
