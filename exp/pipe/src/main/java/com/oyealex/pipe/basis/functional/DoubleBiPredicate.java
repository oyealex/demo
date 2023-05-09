package com.oyealex.pipe.basis.functional;

import java.util.Objects;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型double
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface DoubleBiPredicate<T> {
    boolean test(double doubleValue, T value);

    default DoubleBiPredicate<T> and(DoubleBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (double doubleValue, T value) -> test(doubleValue, value) && other.test(doubleValue, value);
    }

    default DoubleBiPredicate<T> or(DoubleBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (double doubleValue, T value) -> test(doubleValue, value) || other.test(doubleValue, value);
    }

    default DoubleBiPredicate<T> negate() {
        return (double doubleValue, T value) -> !test(doubleValue, value);
    }
}
