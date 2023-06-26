package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型int
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface IntBiFunction<T, R> extends BiFunction<Integer, T, R> {
    R apply(int intValue, T value);

    @Override
    default R apply(Integer intValue, T value) {
        return apply(intValue.intValue(), value);
    }

    default <V> IntBiFunction<T, V> andThen(IntBiFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (int intValue, T value) -> after.apply(intValue, apply(intValue, value));
    }
}
