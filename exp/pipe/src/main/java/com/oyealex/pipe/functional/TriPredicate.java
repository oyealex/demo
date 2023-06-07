package com.oyealex.pipe.functional;

import java.util.Objects;

/**
 * 支持三个参数的返回布尔值的函数接口
 *
 * @param <F> 第一个元素类型
 * @param <S> 第二个元素类型
 * @param <T> 第三个元素类型
 * @author oyealex
 * @since 2023-03-04
 */
@FunctionalInterface
public interface TriPredicate<F, S, T> {
    boolean test(F first, S second, T third);

    default TriPredicate<F, S, T> and(TriPredicate<? super F, ? super S, ? super T> other) {
        Objects.requireNonNull(other);
        return (F first, S second, T third) -> test(first, second, third) && other.test(first, second, third);
    }

    default TriPredicate<F, S, T> negate() {
        return (F first, S second, T third) -> !test(first, second, third);
    }

    default TriPredicate<F, S, T> or(TriPredicate<? super F, ? super S, ? super T> other) {
        Objects.requireNonNull(other);
        return (F first, S second, T third) -> test(first, second, third) || other.test(first, second, third);
    }
}
