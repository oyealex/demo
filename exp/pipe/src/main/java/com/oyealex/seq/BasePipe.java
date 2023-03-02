package com.oyealex.seq;

import java.util.Iterator;

/**
 * BasePipe
 *
 * @author oyealex
 * @since 2023-03-03
 */
public interface BasePipe<T, P extends BasePipe<T, P>> extends AutoCloseable {
    Iterator<T> iterator();

    P onClose(Runnable closeHandler);

    @Override
    void close() throws Exception;
}
