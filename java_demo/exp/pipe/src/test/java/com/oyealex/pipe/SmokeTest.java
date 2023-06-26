package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipe;
import org.junit.jupiter.api.Test;

import static com.oyealex.pipe.policy.PartitionPolicy.END;
import static com.oyealex.pipe.policy.PartitionPolicy.IN;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestFixture {
    @Test
    void smoke() {
        System.out.println(infiniteIntegerStrPipe().limit(10)
            .partitionOrderly((order, ignored) -> order == 7 ? END : IN)
            .map(Pipe::toList)
            .toList());
    }
}
