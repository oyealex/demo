package com.oyealex.pipe.functional;

import java.util.Objects;

/**
 * 支持三个参数的无返回值函数接口
 *
 * @param <F> 第一个元素类型
 * @param <S> 第二个元素类型
 * @param <T> 第三个元素类型
 * @author oyealex
 * @since 2023-03-04
 */
@FunctionalInterface
public interface TriConsumer<F, S, T> {
    void accept(F first, S second, T third);

    default TriConsumer<F, S, T> andThen(TriConsumer<? super F, ? super S, ? super T> after) {
        Objects.requireNonNull(after);
        return (F first, S second, T third) -> {
            accept(first, second, third);
            after.accept(first, second, third);
        };
    }
}
