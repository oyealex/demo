package com.oyealex.pipe;

import org.junit.jupiter.api.Test;

import java.util.Spliterator;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestFixture {
    @Test
    void smoke() {
        Spliterator<String> split = integerStrPipe().limit(10).toSpliterator();
        try {
            split.tryAdvance(ignored -> {
                throw new RuntimeException();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        split.tryAdvance(System.out::println);
        split.tryAdvance(System.out::println);
        split.tryAdvance(System.out::println);
        split.forEachRemaining(System.out::println);
        split.forEachRemaining(System.out::println);
    }

    @Test
    void normal() {
        Spliterator<String> split = integerStrPipe().limit(10).toList().spliterator();
        try {
            split.tryAdvance(ignored -> {
                throw new RuntimeException();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        split.tryAdvance(System.out::println);
        split.tryAdvance(System.out::println);
        split.tryAdvance(System.out::println);
        split.forEachRemaining(System.out::println);
        split.forEachRemaining(System.out::println);
    }
}
