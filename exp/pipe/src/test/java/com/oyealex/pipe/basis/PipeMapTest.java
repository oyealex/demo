package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.functional.IntBiFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * 针对Pipe的映射类方法的测试。
 *
 * @author oyealex
 * @see Pipe#map(Function)
 * @see Pipe#mapEnumerated(IntBiFunction)
 * @see Pipe#mapToInt(ToIntFunction)
 * @see Pipe#mapToLong(ToLongFunction)
 * @see Pipe#mapToDouble(ToDoubleFunction)
 * @see Pipe#flatMap(Function)
 * @see Pipe#flatMapToInt(Function)
 * @see Pipe#flatMapToLong(Function)
 * @see Pipe#flatMapToDouble(Function)
 * @since 2023-04-28
 */
class PipeMapTest extends PipeTestBase {
    @Test
    @DisplayName("能够将元素正确的转换为其他类型")
    void should_map_element_to_another_type_rightly() {
        List<Integer> res = Pipes.of(ELEMENTS).map(String::length).toList();
        Assertions.assertEquals(stream(ELEMENTS).map(String::length).collect(toList()), res);
    }
}
