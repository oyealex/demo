package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        System.out.println(Arrays.toString(Pipes.of("1", "2", "3").toArray(String[]::new)));
    }
}
