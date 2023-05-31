package com.oyealex.pipe;

import org.junit.jupiter.api.Test;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestFixture {
    @Test
    void smoke() {
        System.out.println(infiniteIntegerPipe().limit(100).groupAndCount(value -> value % 3));
    }
}
