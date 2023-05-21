package com.oyealex.pipe;

import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.api.policy.PartitionPolicy;
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
        System.out.println(seqStrPipe().limit(10).partition(ignored -> PartitionPolicy.END).map(Pipe::toList).toList());
        System.out.println(seqStrPipe().limit(10).partitionOrderly((index, ignored) -> (index & 1) == 1 ? PartitionPolicy.END : PartitionPolicy.IN).map(Pipe::toList).toList());
    }
}
