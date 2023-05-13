package com.oyealex.pipe.utils;

import java.util.Comparator;
import java.util.function.Function;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;

/**
 * CheckUtil
 *
 * @author oyealex
 * @since 2023-05-11
 */
public final class MiscUtil {
    private MiscUtil() {
    }

    public static void checkArraySize(long size) {
        if (size >= Integer.MAX_VALUE - 8) { // 数组最大值
            throw new IllegalArgumentException("Pipe size exceeds max array size");
        }
    }

    public static boolean isStdIdentify(Function<?, ?> function) {
        return function == Function.identity();
    }

    public static boolean isStdNaturalOrder(Comparator<?> comparator) {
        return comparator == null || comparator == naturalOrder();
    }

    public static boolean isStdReverseOrder(Comparator<?> comparator) {
        return comparator == reverseOrder();
    }

    @SuppressWarnings("unchecked")
    public static <T> Comparator<? super T> naturalOrderIfNull(Comparator<? super T> comparator) {
        return comparator == null ? (Comparator<? super T>) naturalOrder() : comparator;
    }

    @SuppressWarnings("unchecked")
    public static <T> Comparator<? super T> optimizedReverseOrder(Comparator<? super T> comparator) {
        if (isStdNaturalOrder(comparator)) {
            return (Comparator<? super T>) reverseOrder();
        } else if (isStdReverseOrder(comparator)) {
            return (Comparator<? super T>) naturalOrder();
        } else {
            return comparator.reversed();
        }
    }
}
