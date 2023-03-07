package com.oyealex.seq;

import java.util.Arrays;
import java.util.List;

/**
 * Main
 *
 * @author oyealex
 * @since 2023-03-03
 */
public class Main {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        Pipe.from(list)
            .sorted()
            .map(String::toUpperCase)
            .reversed()
            .peek(System.out::println)
            .peekEnumerated((index, value) -> System.out.println("the " + index + " element is " + value))
            .prepend("1", "2", "3")
            .append("4", "5", "6")
            .partition(2)
            .count();
    }
}
