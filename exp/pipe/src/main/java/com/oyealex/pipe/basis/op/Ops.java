package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;
import com.oyealex.pipe.basis.functional.IntBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;

import java.util.Comparator;
import java.util.Iterator;
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

    public static <T> Op<T> filterEnumeratedOp(Op<T> op, LongBiPredicate<? super T> predicate) {
        return new FilterEnumeratedOp<>(op, predicate);
    }

    public static <T> Op<T> filterOp(Op<T> op, Predicate<? super T> predicate) {
        return new FilterOp<>(op, predicate);
    }

    public static <T> TerminalOp<T, Void> forEachOp(Consumer<? super T> action) {
        return new ForEachOp<>(action);
    }

    public static <T> Op<T> sliceOp(Op<T> op, long skip, long limit) {
        return new SliceOp<>(op, skip, limit);
    }

    public static <IN, OUT> ChainedOp<IN, OUT> mapOp(Op<OUT> op, Function<? super IN, ? extends OUT> mapper) {
        return new MapOp<>(op, mapper);
    }

    public static <T, R> Op<T> mapEnumeratedOp(Op<R> op, LongBiFunction<? super T, ? extends R> mapper) {
        return new MapEnumeratedOp<>(op, mapper);
    }

    public static <T, R> Op<T> flatMapOP(Op<R> op, Function<? super T, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapOp<>(op, mapper);
    }

    public static <T> Op<T> distinctOp(Op<T> op) {
        return new DistinctOp<>(op);
    }

    public static <T, R> Op<T> distinctByOp(Op<T> op, Function<? super T, ? extends R> mapper) {
        return new DistinctByOp<>(op, mapper);
    }

    public static <T> Op<T> sortOp(Op<T> op, Comparator<? super T> comparator) {
        return new SortOp<>(op, comparator);
    }

    public static <T> Op<T> prependOp(Op<T> op, Iterator<? extends T> iterator) {
        return new PrependOp<>(op, iterator);
    }

    public static <T> Op<T> appendOp(Op<T> op, Iterator<? extends T> iterator) {
        return new AppendOp<>(op, iterator);
    }

    public static <T> Op<T> partitionOp(Op<Pipe<T>> op, int size) {
        return new PartitionOp<>(op, size);
    }

    public static <T> TerminalOp<T, Void> forEachEnumeratedOp(IntBiConsumer<? super T> action) {
        return new ForEachEnumeratedOp<>(action);
    }

    public static <T> TerminalOp<T, Optional<T>> minMaxOp(boolean requireMin, Comparator<? super T> comparator) {
        return new MinMaxOp<>(requireMin, comparator);
    }

    public static <OUT> Op<OUT> keepOrDropWhileOp(Op<OUT> op, boolean isKeep, Predicate<? super OUT> predicate) {
        return isKeep ? new KeepOrDropWhileOps.KeepWhileOp<>(op, predicate) :
            new KeepOrDropWhileOps.DropWhileOp<>(op, predicate);
    }
}
