package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 接收两个参数的无返回值函数接口，其中第一个参数为基本类型long
 *
 * @author oyealex
 * @since 2023-03-05
 */
@FunctionalInterface
public interface LongBiConsumer<T> extends BiConsumer<Long, T> {
    void accept(long longValue, T value);

    @Override
    default void accept(Long longValue, T value) {
        accept(longValue.longValue(), value);
    }

    default LongBiConsumer<T> andThen(LongBiConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (longValue, value) -> {
            accept(longValue, value);
            after.accept(longValue, value);
        };
    }
}
