package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;
import com.oyealex.pipe.basis.functional.IntBiConsumer;
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
public class Ops {
    private Ops() {
        throw new IllegalStateException("no instance available");
    }

    public static <T> TerminalOp<T, Long> countOp() {
        return new CountOp<>();
    }

    public static <T> Op<T> filterEnumeratedOp(Op<T> nextOp, LongBiPredicate<? super T> predicate) {
        return new FilterEnumeratedOp<>(nextOp, predicate);
    }

    public static <T> Op<T> filterOp(Op<T> nextOp, Predicate<? super T> predicate) {
        return new FilterOp<>(nextOp, predicate);
    }

    public static <T> TerminalOp<T, Void> forEachOp(Consumer<? super T> action) {
        return new ForEachOp<>(action);
    }

    public static <T> Op<T> sliceOp(Op<T> nextOp, long skip, long limit) {
        return new SliceOp<>(nextOp, skip, limit);
    }

    public static <IN, OUT> ChainedOp<IN, OUT> mapOp(Op<OUT> nextOp, Function<? super IN, ? extends OUT> mapper) {
        return new MapOp<>(nextOp, mapper);
    }

    public static <T, R> Op<T> mapEnumeratedOp(Op<R> nextOp, LongBiFunction<? super T, ? extends R> mapper) {
        return new MapEnumeratedOp<>(nextOp, mapper);
    }

    public static <T, R> Op<T> flatMapOP(Op<R> nextOp, Function<? super T, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapOp<>(nextOp, mapper);
    }

    public static <T> Op<T> distinctOp(Op<T> op) {
        return new DistinctOp<>(op);
    }

    public static <T, R> Op<T> distinctByOp(Op<T> nextOp, Function<? super T, ? extends R> mapper) {
        return new DistinctByOp<>(nextOp, mapper);
    }

    public static <T> Op<T> sortOp(Op<T> nextOp, Comparator<? super T> comparator) {
        return new SortOp<>(nextOp, comparator);
    }

    public static <T> Op<T> partitionOp(Op<Pipe<T>> nextOp, int size) {
        return new PartitionOp<>(nextOp, size);
    }

    public static <T> TerminalOp<T, Void> forEachEnumeratedOp(LongBiConsumer<? super T> action) {
        return new ForEachEnumeratedOp<>(action);
    }

    public static <T> TerminalOp<T, Optional<T>> minMaxOp(boolean requireMin, Comparator<? super T> comparator) {
        return new MinMaxOp<>(requireMin, comparator);
    }

    public static <T> Op<T> keepOrDropWhileOp(Op<T> nextOp, boolean isKeep, Predicate<? super T> predicate) {
        return isKeep ? new KeepOrDropWhileOps.KeepWhileOp<>(nextOp, predicate) :
            new KeepOrDropWhileOps.DropWhileOp<>(nextOp, predicate);
    }

    public static <T> Op<T> keepOrDropWhileEnumeratedOp(Op<T> nextOp, boolean isKeep,
        LongBiPredicate<? super T> predicate) {
        return isKeep ? new KeepOrDropWhileEnumeratedOps.KeepWhileOp<>(nextOp, predicate) :
            new KeepOrDropWhileEnumeratedOps.DropWhileOp<>(nextOp, predicate);
    }

    public static <T> Op<T> peekOp(Op<T> nextOp, Consumer<? super T> consumer) {
        return new PeekOp<>(nextOp, consumer);
    }

    public static <T> Op<T> peekEnumeratedOp(Op<T> nextOp, LongBiConsumer<? super T> consumer) {
        return new PeekEnumeratedOp<>(nextOp, consumer);
    }
}
