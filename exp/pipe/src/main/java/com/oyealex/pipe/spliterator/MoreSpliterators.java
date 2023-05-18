package com.oyealex.pipe.spliterator;

import java.util.Spliterator;

/**
 * MoreSpliterators
 *
 * @author oyealex
 * @since 2023-05-18
 */
public class MoreSpliterators {
    private MoreSpliterators() {
        throw new IllegalStateException("no instance available");
    }

    public static <T> Spliterator<T> singleton(T singleton) {
        return new SingletonSpliterator<>(singleton);
    }

    public static <T> Spliterator<T> constant(T constant, int count) {
        return new ConstantSpliterator<>(constant, count);
    }

    public static <T, S extends Spliterator<T>> Spliterator<T> concat(S head, S tail) {
        return new ConcatSpliterator<>(head, tail);
    }
}
