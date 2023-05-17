package com.oyealex.pipe.assist;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;

/**
 * CachedSplitIterator
 *
 * @author oyealex
 * @since 2023-05-17
 */
public interface CachedSplitIterator<T> extends Iterator<T> {
    default T takeNext() {
        return next();
    }

    T getNext();

    static <T> CachedSplitIterator<T> fromSpliterator(Spliterator<? extends T> spliterator) {
        return new CachedSplitIterator<>() {
            boolean valueReady = false;

            T nextElement;

            private void accept(T t) {
                valueReady = true;
                nextElement = t;
            }

            @Override
            public boolean hasNext() {
                if (!valueReady) {
                    spliterator.tryAdvance(this::accept);
                }
                return valueReady;
            }

            @Override
            public T next() {
                if (!valueReady && !hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    valueReady = false;
                    return nextElement;
                }
            }

            @Override
            public T getNext() {
                if (!valueReady && !hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    return nextElement;
                }
            }
        };
    }
}
