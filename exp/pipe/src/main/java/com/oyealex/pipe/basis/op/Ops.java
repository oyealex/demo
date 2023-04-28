package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.IntBiPredicate;
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

    public static <T> FilterEnumeratedLongOp<T> filterEnumeratedLongOp(Op<T> op, LongBiPredicate<? super T> predicate) {
        return new FilterEnumeratedLongOp<>(op, predicate);
    }

    public static <T> FilterEnumeratedOp<T> filterEnumeratedOp(Op<T> op, IntBiPredicate<? super T> predicate) {
        return new FilterEnumeratedOp<>(op, predicate);
    }

    public static <T> FilterOp<T> filterOp(Op<T> op, Predicate<? super T> predicate) {
        return new FilterOp<>(op, predicate);
    }

    public static <T> ForEachOp<T> foreachOp(Consumer<? super T> action) {
        return new ForEachOp<>(action);
    }

    public static <T> LimitOp<T> gettLimitOp(Op<T> op, long size) {
        return new LimitOp<>(op, size);
    }

    public static <IN, OUT> MapOp<IN, OUT> mapOp(Op<OUT> op, Function<? super IN, ? extends OUT> mapper) {
        return new MapOp<>(op, mapper);
    }
}
