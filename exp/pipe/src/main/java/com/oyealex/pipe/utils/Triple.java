package com.oyealex.pipe.utils;

import java.util.Objects;
import java.util.function.Function;

/**
 * Triple
 *
 * @author oyealex
 * @since 2023-05-23
 */
public final class Triple<F, S, T> {
    public final F first;

    public final S second;

    public final T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public <FF, SS, TT> Triple<FF, SS, TT> map(Function<? super F, ? extends FF> firstMapper,
        Function<? super S, ? extends SS> secondMapper, Function<? super T, ? extends TT> thirdMapper) {
        return new Triple<>(firstMapper.apply(first), secondMapper.apply(second), thirdMapper.apply(third));
    }

    public <E> Triple<E, S, T> mapFirst(Function<? super F, ? extends E> mapper) {
        return new Triple<>(mapper.apply(first), second, third);
    }

    public <E> Triple<F, E, T> mapSecond(Function<? super S, ? extends E> mapper) {
        return new Triple<>(first, mapper.apply(second), third);
    }

    public <E> Triple<F, S, E> mapThird(Function<? super T, ? extends E> mapper) {
        return new Triple<>(first, second, mapper.apply(third));
    }

    public Tuple<S, T> dropFirst() {
        return new Tuple<>(second, third);
    }

    public Tuple<F, T> dropSecond() {
        return new Tuple<>(first, third);
    }

    public Tuple<F, S> dropThird() {
        return new Tuple<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) && Objects.equals(second, triple.second) &&
            Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third + ")";
    }
}
