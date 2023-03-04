package com.oyealex.seq;

/**
 * BasePipe
 *
 * @author oyealex
 * @since 2023-03-03
 */
public interface BasePipe<P extends BasePipe<P>> extends AutoCloseable {
    P onClose(Runnable closeAction);

    @Override
    void close();
}
