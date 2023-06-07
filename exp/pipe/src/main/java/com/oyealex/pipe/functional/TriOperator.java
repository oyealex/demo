package com.oyealex.pipe.functional;

import java.util.Comparator;
import java.util.Objects;

/**
 * 接收三个同类型参数并且返回同类型参数的函数接口
 *
 * @param <T> 参数和返回值类型
 * @author oyealex
 * @since 2023-03-04
 */
@FunctionalInterface
public interface TriOperator<T> extends TriFunction<T, T, T, T> {
    static <T> TriOperator<T> minBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (first, second, third) -> {
            T min = comparator.compare(first, second) <= 0 ? first : second;
            return comparator.compare(min, third) <= 0 ? min : third;
        };
    }

    static <T> TriOperator<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (first, second, third) -> {
            T max = comparator.compare(first, second) >= 0 ? first : second;
            return comparator.compare(max, third) >= 0 ? max : third;
        };
    }
}
