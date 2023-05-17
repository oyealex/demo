package com.oyealex.pipe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestBase {
    @Test
    @DisplayName("smoke")
    void smoke() {
        System.out.println(seqIntegerPipe().limit(10).group(i -> i & 1));
        System.out.println(seqIntegerPipe().limit(10).groupAndThen(i -> i & 1, (k,v) -> v.size()));
    }
}
