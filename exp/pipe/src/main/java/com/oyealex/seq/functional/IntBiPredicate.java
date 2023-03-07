package com.oyealex.seq.functional;

import java.util.Objects;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型int
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface IntBiPredicate<T> {
    boolean test(int intValue, T value);

    default IntBiPredicate<T> and(IntBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (int intValue, T value) -> test(intValue, value) && other.test(intValue, value);
    }

    default IntBiPredicate<T> or(IntBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (int intValue, T value) -> test(intValue, value) || other.test(intValue, value);
    }

    default IntBiPredicate<T> negate() {
        return (int intValue, T value) -> !test(intValue, value);
    }
}
