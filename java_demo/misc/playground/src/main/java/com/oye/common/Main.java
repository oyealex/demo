package com.oye.common;

import java.util.stream.Stream;

/**
 * Main
 *
 * @author oyealex
 * @since 2023-04-24
 */
public class Main {
    public static void main(String[] args) {
        long count = Stream.of(0, 1, 5, 74, 4, 9, 3, 54, 9, 4, 9, 5, 9, 4, 3).peek(System.out::println)
            .sorted()
            .map(a -> a + 1)
            .findFirst().get();
        System.out.println(count);
    }
}
