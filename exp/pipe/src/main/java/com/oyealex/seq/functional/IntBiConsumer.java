package com.oyealex.seq.functional;

import java.util.Objects;

/**
 * 接收两个参数的无返回值函数接口，其中第一个参数为基本类型int
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface IntBiConsumer<T> {
    void accept(int intValue, T value);

    default IntBiConsumer<T> andThen(IntBiConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (intValue, value) -> {
            accept(intValue, value);
            after.accept(intValue, value);
        };
    }
}
