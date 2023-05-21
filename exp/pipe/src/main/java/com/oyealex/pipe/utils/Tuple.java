package com.oyealex.pipe.utils;

import java.util.function.Function;

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

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public Tuple<S, F> swap() {
        return new Tuple<>(second, first);
    }

    public <T> Tuple<T, S> mapFirst(Function<? super F, ? extends T> mapper) {
        return new Tuple<>(mapper.apply(first), second);
    }

    public <T> Tuple<F, T> mapSecond(Function<? super S, ? extends T> mapper) {
        return new Tuple<>(first, mapper.apply(second));
    }

    public <NF, NS> Tuple<NF, NS> map(Function<? super F, ? extends NF> firstMapper,
        Function<? super S, ? extends NS> secondMapper) {
        return new Tuple<>(firstMapper.apply(first), secondMapper.apply(second));
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}
