package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型long
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface LongBiFunction<T, R> extends BiFunction<Long, T, R> {
    R apply(long longValue, T value);

    @Override
    default R apply(Long longValue, T value) {
        return apply(longValue.longValue(), value);
    }

    default <V> LongBiFunction<T, V> andThen(LongBiFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (long longValue, T value) -> after.apply(longValue, apply(longValue, value));
    }
}
