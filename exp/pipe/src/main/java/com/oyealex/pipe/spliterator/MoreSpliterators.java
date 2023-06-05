package com.oyealex.pipe.spliterator;

import com.oyealex.pipe.utils.NoInstance;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.ORDERED;

/**
 * MoreSpliterators
 *
 * @author oyealex
 * @since 2023-05-18
 */
public final class MoreSpliterators extends NoInstance {
    public static <T> Spliterator<T> singleton(T singleton) {
        return new SingletonSpliterator<>(singleton);
    }

    public static <T> Spliterator<T> constant(T constant, int count) {
        return new ConstantSpliterator<>(constant, count);
    }

    public static <T, S extends Spliterator<T>> Spliterator<T> concat(S head, S tail) {
        return new ConcatSpliterator<>(head, tail);
    }

    public static <T> Spliterator<T> generate(Supplier<? extends T> supplier) {
        return new Spliterators.AbstractSpliterator<T>(MAX_VALUE, IMMUTABLE) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                action.accept(supplier.get());
                return true;
            }
        };
    }

    public static <T> Spliterator<T> iterate(T seed, UnaryOperator<T> generator) {
        return new Spliterators.AbstractSpliterator<T>(MAX_VALUE, ORDERED | IMMUTABLE) {
            private T previous;

            private boolean started;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                requireNonNull(action);
                T value;
                if (started) {
                    value = generator.apply(previous);
                } else {
                    value = seed;
                    started = true;
                }
                action.accept(previous = value);
                return true;
            }
        };
    }
}
