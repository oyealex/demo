package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 接收两个参数的返回布尔值的函数接口，其中第一个参数为基本类型double
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface DoubleBiFunction<T, R> extends BiFunction<Double, T,R> {
    R apply(double doubleValue, T value);

    @Override
    default R apply(Double doubleValue, T value) {
        return apply(doubleValue.doubleValue(), value);
    }

    default <V> DoubleBiFunction<T, V> andThen(DoubleBiFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (double doubleValue, T value) -> after.apply(doubleValue, apply(doubleValue, value));
    }
}
