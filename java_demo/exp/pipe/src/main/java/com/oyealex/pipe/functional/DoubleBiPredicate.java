package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型double
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface DoubleBiPredicate<T> extends BiPredicate<Double, T> {
    boolean test(double doubleValue, T value);

    @Override
    default boolean test(Double doubleValue, T value) {
        return test(doubleValue.doubleValue(), value);
    }

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
