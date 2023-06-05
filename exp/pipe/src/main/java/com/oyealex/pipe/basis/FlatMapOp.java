package com.oyealex.pipe.basis;

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
abstract class FlatMapOp<T, R> extends RefPipe<T, R> {
    protected FlatMapOp(RefPipe<?, ? extends T> prePipe) {
        super(prePipe, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_SIZED);
    }

    static class Normal<T, R> extends FlatMapOp<T, R> {
        private final Function<? super T, ? extends Pipe<? extends R>> mapper;

        Normal(RefPipe<?, ? extends T> prePipe, Function<? super T, ? extends Pipe<? extends R>> mapper) {
            super(prePipe);
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<R> nextOp) {
            return new InternalOp<T, R>(nextOp) {
                @Override
                protected Pipe<? extends R> mapToPipe(T value) {
                    return mapper.apply(value);
                }
            };
        }
    }

    static class Orderly<T, R> extends FlatMapOp<T, R> {
        private final LongBiFunction<? super T, ? extends Pipe<? extends R>> mapper;

        Orderly(RefPipe<?, ? extends T> prePipe, LongBiFunction<? super T, ? extends Pipe<? extends R>> mapper) {
            super(prePipe);
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<R> nextOp) {
            return new InternalOp<T, R>(nextOp) {
                private long index = 0L;

                @Override
                protected Pipe<? extends R> mapToPipe(T value) {
                    return mapper.apply(index++, value);
                }
            };
        }
    }

    private static abstract class InternalOp<T, R> extends ChainedOp.ShortCircuitRecorded<T, R> {
        private InternalOp(Op<R> nextOp) {
            super(nextOp);
        }

        @Override
        public void begin(long size) {
            nextOp.begin(-1);
        }

        @Override
        public void accept(T value) {
            // 新的流水线可能包含了关闭方法，需要确保调用close方法
            try (Pipe<? extends R> pipe = mapToPipe(value)) {
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

        protected abstract Pipe<? extends R> mapToPipe(T value);
    }
}
