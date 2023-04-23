package com.oyealex.pipe;

import java.util.Iterator;

/**
 * SourcePipe
 *
 * @author oyealex
 * @since 2023-03-04
 */
public class SourcePipe<T> {
    private final Iterator<T> source;

    SourcePipe(Iterator<T> source) {
        this.source = source;
    }
}
