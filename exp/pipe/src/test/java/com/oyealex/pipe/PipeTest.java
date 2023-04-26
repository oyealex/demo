package com.oyealex.pipe;

import org.junit.jupiter.api.Test;

/**
 * PipeTest
 *
 * @author oyealex
 * @since 2023-04-27
 */
class PipeTest {
    @Test
    void smoke() {
        System.out.println(
            Pipe.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(i -> (i & 1) == 1).map(String::valueOf).toList());
    }
}