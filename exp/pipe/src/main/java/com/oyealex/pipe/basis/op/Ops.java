package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;
import com.oyealex.pipe.basis.functional.IntBiFunction;
import com.oyealex.pipe.basis.functional.IntBiPredicate;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;

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

    public static <T> Op<T> filterEnumeratedLongOp(Op<T> op, LongBiPredicate<? super T> predicate) {
        return new FilterEnumeratedLongOp<>(op, predicate);
    }

    public static <T> Op<T> filterEnumeratedOp(Op<T> op, IntBiPredicate<? super T> predicate) {
        return new FilterEnumeratedOp<>(op, predicate);
    }

    public static <T> Op<T> filterOp(Op<T> op, Predicate<? super T> predicate) {
        return new FilterOp<>(op, predicate);
    }

    public static <T> TerminalOp<T, Void> foreachOp(Consumer<? super T> action) {
        return new ForEachOp<>(action);
    }

    public static <T> Op<T> gettLimitOp(Op<T> op, long size) {
        return new LimitOp<>(op, size);
    }

    public static <IN, OUT> ChainedOp<IN, OUT> mapOp(Op<OUT> op, Function<? super IN, ? extends OUT> mapper) {
        return new MapOp<>(op, mapper);
    }

    public static <OUT, R> Op<OUT> mapEnumeratedOp(Op<R> op, IntBiFunction<? super OUT, ? extends R> mapper) {
        return new MapEnumeratedOp<>(op, mapper);
    }

    public static <OUT, R> Op<OUT> mapEnumeratedLongOp(Op<R> op, LongBiFunction<? super OUT, ? extends R> mapper) {
        return new MapEnumeratedLongOp<>(op, mapper);
    }

    public static <IN> Op<IN> skipOp(Op<IN> op, long size) {
        return new SkipOp<>(op, size);
    }

    public static <IN> Op<IN> flatMapOP(Op<IN> op, Function<? super IN, ? extends Pipe<? extends IN>> mapper) {
        return new FlatMapOp<>(op, mapper);
    }
}
