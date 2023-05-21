package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.Test;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestBase {
    @Test
    void smoke() {
        Pipes.of("This", "is", "an", "example", "of", "Pipe")
            .takeIf(word -> word.length() < 2)
            .sort()
            .limit(2)
            .toList();
    }
}
