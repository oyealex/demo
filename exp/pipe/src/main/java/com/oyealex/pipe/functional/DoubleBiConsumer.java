package com.oyealex.pipe.functional;

import java.util.Objects;

/**
 * 接收两个参数的无返回值函数接口，其中第一个参数为基本类型double
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface DoubleBiConsumer<T> {
    void accept(double doubleValue, T value);

    default DoubleBiConsumer<T> andThen(DoubleBiConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (doubleValue, value) -> {
            accept(doubleValue, value);
            after.accept(doubleValue, value);
        };
    }
}
