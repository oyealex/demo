package com.oyealex.pipe.basis;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 一些杂项
 *
 * @author oyealex
 * @since 2023-04-28
 */
class Misc {
    private Misc() {
        throw new IllegalStateException("no instance available");
    }

    private static final Iterator<?> EMPTY_ITERATOR = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };

    @SuppressWarnings("unchecked")
    static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }

    static Runnable composeAction(Runnable action, Runnable anotherAction) {
        return () -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                try {
                    anotherAction.run();
                } catch (Throwable anotherThrowable) {
                    try {
                        throwable.addSuppressed(anotherThrowable);
                    } catch (Throwable ignore) {}
                }
                throw throwable;
            }
            anotherAction.run();
        };
    }
}
