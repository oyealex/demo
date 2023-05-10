package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.Spliterator;
import java.util.function.Function;

import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;

/**
 * KeepOrDropWhileStage
 *
 * @author oyealex
 * @since 2023-05-10
 */
abstract class FlatMapStage<T, R> extends RefPipe<T, R> {
    protected FlatMapStage(RefPipe<?, ? extends T> prePipe) {
        super(prePipe, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_SIZED);
    }

    static class Normal<T, R> extends FlatMapStage<T, R> {
        private final Function<? super T, ? extends Pipe<? extends R>> mapper;

        Normal(RefPipe<?, ? extends T> prePipe, Function<? super T, ? extends Pipe<? extends R>> mapper) {
            super(prePipe);
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<R> nextOp) {
            return new FlatMapOp<>(nextOp) {
                @Override
                protected Pipe<? extends R> mapToPipe(T var) {
                    return mapper.apply(var);
                }
            };
        }
    }

    static class Orderly<T, R> extends FlatMapStage<T, R> {
        private final LongBiFunction<? super T, ? extends Pipe<? extends R>> mapper;

        Orderly(RefPipe<?, ? extends T> prePipe, LongBiFunction<? super T, ? extends Pipe<? extends R>> mapper) {
            super(prePipe);
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<R> nextOp) {
            return new FlatMapOp<>(nextOp) {
                private long index = 0L;

                @Override
                protected Pipe<? extends R> mapToPipe(T var) {
                    return mapper.apply(index++, var);
                }
            };
        }
    }

    private static abstract class FlatMapOp<T, R> extends ChainedOp<T, R> {
        private boolean isShortCircuitRequested;

        public FlatMapOp(Op<R> nextOp) {
            super(nextOp);
            isShortCircuitRequested = false;
        }

        @Override
        public void begin(long size) {
            nextOp.begin(-1);
        }

        @Override
        public void accept(T var) {
            // 新的流水线可能包含了关闭方法，需要确保调用close方法
            try (Pipe<? extends R> pipe = mapToPipe(var)) {
                if (pipe == null) {
                    return;
                }
                if (isShortCircuitRequested) {
                    // 如果曾经调用过短路方法，则说明可能存在短路操作，此时需要逐个尝试访问新产生的流水线元素
                    Spliterator<? extends R> split = pipe.toSpliterator();
                    while (!nextOp.canShortCircuit() && split.tryAdvance(nextOp)) {
                        // nothing
                    }
                } else {
                    // 非短路则直接批量访问
                    pipe.forEach(nextOp);
                }
            }
        }

        @Override
        public boolean canShortCircuit() {
            // 只要这个方法被调用过，无论结果如何都说明流水线中存在可以短路的操作
            isShortCircuitRequested = true;
            return super.canShortCircuit();
        }

        protected abstract Pipe<? extends R> mapToPipe(T var);
    }
}
