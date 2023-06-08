package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型int
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface IntBiPredicate<T> extends BiPredicate<Integer, T> {
    boolean test(int intValue, T value);

    @Override
    default boolean test(Integer intValue, T value) {
        return test(intValue.intValue(), value);
    }

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
