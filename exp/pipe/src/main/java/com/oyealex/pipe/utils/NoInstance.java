package com.oyealex.pipe.utils;

/**
 * NoInstance
 *
 * @author oyealex
 * @since 2023-05-21
 */
public class NoInstance {
    protected NoInstance() {
        throw new IllegalStateException("no instance available");
    }
}
