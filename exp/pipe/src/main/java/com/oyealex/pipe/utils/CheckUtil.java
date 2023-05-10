package com.oyealex.pipe.utils;

/**
 * CheckUtil
 *
 * @author oyealex
 * @since 2023-05-11
 */
public final class CheckUtil {
    private CheckUtil() {
    }

    public static void checkArraySize(long size) {
        if (size >= Integer.MAX_VALUE - 8) { // 数组最大值
            throw new IllegalArgumentException("Pipe size exceeds max array size");
        }
    }
}
