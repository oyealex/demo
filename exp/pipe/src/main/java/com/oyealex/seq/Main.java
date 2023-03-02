package com.oyealex.seq;

import java.util.Arrays;

/**
 * Main
 *
 * @author oyealex
 * @since 2023-03-03
 */
public class Main {
    public static void main(String[] args) {
        test("", "");
    }

    // @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> void test(T... values) {
        System.out.println(Arrays.toString(values));
    }
}
