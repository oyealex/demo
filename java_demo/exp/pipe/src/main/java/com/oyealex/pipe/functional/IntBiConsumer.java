package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 接收两个参数的无返回值函数接口，其中第一个参数为基本类型int
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface IntBiConsumer<T> extends BiConsumer<Integer, T> {
    void accept(int intValue, T value);

    @Override
    default void accept(Integer intValue, T value) {
        accept(intValue.intValue(), value);
    }

    default IntBiConsumer<T> andThen(IntBiConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (intValue, value) -> {
            accept(intValue, value);
            after.accept(intValue, value);
        };
    }
}
