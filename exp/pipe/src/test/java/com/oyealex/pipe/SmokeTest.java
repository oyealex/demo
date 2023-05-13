package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        System.out.println(Pipes.of(1, 2, 3).peek(System.out::print).anyNull());
        System.out.println(Pipes.of(1, 2, 3).peek(System.out::print).allNull());
        System.out.println(Pipes.of(1, 2, 3).peek(System.out::print).noneNull());
        System.out.println();
        System.out.println(Pipes.of(1, null, 2, 3).peek(System.out::print).anyNull());
        System.out.println(Pipes.of(1, null, 2, 3).peek(System.out::print).allNull());
        System.out.println(Pipes.of(1, null, 2, 3).peek(System.out::print).noneNull());
        System.out.println();
        System.out.println(Pipes.<Integer>of(null, null, null).peek(System.out::print).anyNull());
        System.out.println(Pipes.<Integer>of(null, null, null).peek(System.out::print).allNull());
        System.out.println(Pipes.<Integer>of(null, null, null).peek(System.out::print).noneNull());
    }
}
