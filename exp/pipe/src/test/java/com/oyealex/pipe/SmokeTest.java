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
        System.out.println(evenPipe().limit(10).mergeAlternately(oddPipe().limit(10)).toList());
        System.out.println(
            evenPipe().mergeAlternately(oddPipe()).peek(var -> System.out.println("before " + var)).limit(20)
                .peek(var -> System.out.println("after " + var)).toList());
    }
}
