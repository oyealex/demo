package com.oyealex.pipe.basis;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * SimpleSpliterators
 *
 * @author oyealex
 * @since 2023-05-12
 */
final class SimpleSpliterators {
    private SimpleSpliterators() {
        throw new IllegalStateException("no instance available");
    }

    static <T> Spliterator<T> singleton(T var) {
        return new Spliterator<T>() {
            private T single = var;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if (single == null) {
                    return false;
                }
                T var = single;
                single = null;
                action.accept(var);
                return true;
            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return single == null ? 0 : 1;
            }

            @Override
            public int characteristics() {
                return Spliterator.SIZED | Spliterator.SORTED | Spliterator.DISTINCT | Spliterator.ORDERED |
                    Spliterator.SUBSIZED;
            }

            @Override
            public Comparator<? super T> getComparator() {
                return null;
            }
        };
    }
}
