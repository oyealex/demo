package com.oyealex.pipe.base;

import java.util.Iterator;
import java.util.Spliterator;

/**
 * BasePipe
 *
 * @author oyealex
 * @since 2023-05-23
 */
public interface BasePipe<T, P extends BasePipe<T, P>> extends AutoCloseable {
    Iterator<T> toIterator();

    Spliterator<T> toSpliterator();

    P onClose(Runnable closeAction);

    @Override
    void close();

    P debug();
}
