package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestBase {
    @Test
    void smoke() {
        // System.out.println(seqStrPipe().disperse("_").limit(10).toList());
        // System.out.println(seqStrPipe().limit(10).disperse("_").toList());
        // System.out.println(seqStrPipe().limit(10).disperse("_").limit(10).toList());
        System.out.println(Pipes.singleton("1").map(e -> null).println().anyNull());
        System.out.println(Pipes.singleton("1").mapIf(e -> Optional.empty()).println().anyNull());
    }
}
