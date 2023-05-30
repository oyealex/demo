package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

/**
 * 针对流水线切片API的测试。
 *
 * @author oyealex
 * @see Pipe#skip(long)
 * @see Pipe#skip(long, Predicate)
 * @see Pipe#limit(long)
 * @see Pipe#limit(long, Predicate)
 * @see Pipe#slice(long, long)
 * @see Pipe#slice(long, long, Predicate)
 * @since 2023-05-30
 */
class PipeSliceTest extends PipeTestFixture {
    @Test
    void smoke() {
        Predicate<Integer> predicate = value -> {
            System.out.println(value);
            return (value & 1) == 0;
        };
        // System.out.println(infiniteIntegerPipe().limit(30).toList());
        System.out.println(infiniteIntegerPipe().limit(30).skip(5, predicate).toList());
        // System.out.println(infiniteIntegerPipe().limit(30).limit(5, predicate).toList());
        // System.out.println(infiniteIntegerPipe().limit(30).slice(3, 6, predicate).toList());
    }
}
