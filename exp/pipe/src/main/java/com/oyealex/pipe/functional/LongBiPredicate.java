package com.oyealex.pipe.functional;

import java.util.Objects;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型long
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface LongBiPredicate<T> {
    boolean test(long longValue, T value);

    default LongBiPredicate<T> and(LongBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (long longValue, T value) -> test(longValue, value) && other.test(longValue, value);
    }

    default LongBiPredicate<T> or(LongBiPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (long longValue, T value) -> test(longValue, value) || other.test(longValue, value);
    }

    default LongBiPredicate<T> negate() {
        return (long longValue, T value) -> !test(longValue, value);
    }
}
