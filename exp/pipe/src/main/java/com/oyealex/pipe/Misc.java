package com.oyealex.pipe;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 一些杂项
 *
 * @author oyealex
 * @since 2023-04-28
 */
public class Misc {
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
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }
}
