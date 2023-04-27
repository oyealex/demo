package com.oyealex.pipe.functional;

import java.util.Objects;

/**
 * 接收两个参数的无返回值函数接口，其中第一个参数为基本类型long
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface LongBiConsumer<T> {
    void accept(long longValue, T value);

    default LongBiConsumer<T> andThen(LongBiConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (longValue, value) -> {
            accept(longValue, value);
            after.accept(longValue, value);
        };
    }
}
