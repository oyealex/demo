package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestBase {
    @Test
    void smoke() {
        Iterator<String> iterator = seqStrPipe().limit(25).toIterator();
        // System.out.println(Pipes.iterator(iterator).takeLast(10).limit(5).toList());
        System.out.println(Pipes.iterator(iterator).dropLast(10).limit(5).toList());
    }
}
