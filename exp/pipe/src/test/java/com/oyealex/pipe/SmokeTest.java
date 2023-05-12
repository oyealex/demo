package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest {
    @Test
    @DisplayName("smoke")
    void smoke() {
        System.out.println(Pipes.of(1, 2, 3, 4, 5).findFirst());
        System.out.println(Pipes.of(1, 2, 3, 4, 5).findLast());
        System.out.println(Pipes.of(1, 2, 3, 4, 5).shuffle().peek(System.out::print).findLast());
        System.out.println(Pipes.of(1, 2, 3, 4, 5).shuffle().peek(System.out::print).findLast());
        System.out.println(Pipes.of(1, 2, 3, 4, 5).shuffle().max(Comparator.naturalOrder()));
        System.out.println(Pipes.of(1, 2, 3, 4, 5).shuffle().min(Comparator.naturalOrder()));
        System.out.println(Pipes.of(1, 2, 3, 4, 5).max(Comparator.reverseOrder()));
        System.out.println(Pipes.of(1, 2, 3, 4, 5).min(Comparator.reverseOrder()));
    }
}
