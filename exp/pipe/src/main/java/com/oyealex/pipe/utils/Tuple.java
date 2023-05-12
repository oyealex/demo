package com.oyealex.pipe.utils;

/**
 * Tuple
 *
 * @author oyealex
 * @since 2023-05-13
 */
public final class Tuple<F, S> {
    public final F first;

    public final S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}
