package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Ops
 *
 * @author oyealex
 * @since 2023-04-28
 */
class SimpleOps {
    private SimpleOps() {
        throw new IllegalStateException("no instance available");
    }

    public static <T> TerminalOp<T, Long> countOp() {
        return new CountOp<>();
    }

    public static <T> Op<T> keepIfOp(Op<T> nextOp, Predicate<? super T> predicate) {
        return new ChainedOp<>(nextOp) {
            @Override
            public void begin(long size) {
                nextOp.begin(-1);
            }

            @Override
            public void accept(T in) {
                if (predicate.test(in)) {
                    nextOp.accept(in);
                }
            }
        };
    }

    public static <T> Op<T> keepIfOrderlyOp(Op<T> nextOp, LongBiPredicate<? super T> predicate) {
        return new ChainedOp.OrderlyOp<>(nextOp) {
            @Override
            public void begin(long size) {
                nextOp.begin(-1);
            }

            @Override
            public void accept(T in) {
                if (predicate.test(index++, in)) {
                    nextOp.accept(in);
                }
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOp(Consumer<? super T> action) {
        return new ForEachOp<>(action);
    }

    public static <T> Op<T> sliceOp(Op<T> nextOp, long skip, long limit) {
        return new SliceOp<>(nextOp, skip, limit);
    }

    public static <IN, OUT> ChainedOp<IN, OUT> mapOp(Op<OUT> nextOp, Function<? super IN, ? extends OUT> mapper) {
        return new ChainedOp<>(nextOp) {
            @Override
            public void accept(IN in) {
                nextOp.accept(mapper.apply(in));
            }
        };
    }

    public static <T, R> Op<T> mapOrderlyOp(Op<R> nextOp, LongBiFunction<? super T, ? extends R> mapper) {
        return new ChainedOp.OrderlyOp<>(nextOp) {
            @Override
            public void accept(T in) {
                nextOp.accept(mapper.apply(index++, in));
            }
        };
    }

    public static <T> Op<T> partitionOp(Op<Pipe<T>> nextOp, int size) {
        return new PartitionOp<>(nextOp, size);
    }

    public static <T> TerminalOp<T, Void> forEachOrderlyOp(LongBiConsumer<? super T> action) {
        return new ForEachOrderlyOp<>(action);
    }

    public static <T> TerminalOp<T, Optional<T>> minMaxOp(boolean requireMin, Comparator<? super T> comparator) {
        return new MinMaxOp<>(requireMin, comparator);
    }

    public static <T> Op<T> peekOp(Op<T> nextOp, Consumer<? super T> consumer) {
        return new PeekOp<>(nextOp, consumer);
    }

    public static <T> Op<T> peekOrderlyOp(Op<T> nextOp, LongBiConsumer<? super T> consumer) {
        return new PeekOrderlyOp<>(nextOp, consumer);
    }
}
