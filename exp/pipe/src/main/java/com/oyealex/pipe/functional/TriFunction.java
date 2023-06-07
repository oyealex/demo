package com.oyealex.pipe.functional;

import java.util.Objects;
import java.util.function.Function;

/**
 * 支持三个参数的函数接口
 *
 * @param <F> 第一个元素类型
 * @param <S> 第二个元素类型
 * @param <T> 第三个元素类型
 * @param <R> 结果类型
 * @author oyealex
 * @since 2023-03-04
 */
@FunctionalInterface
public interface TriFunction<F, S, T, R> {
    R apply(F first, S second, T third);

    default <V> TriFunction<F, S, T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (F first, S second, T third) -> after.apply(apply(first, second, third));
    }
}
