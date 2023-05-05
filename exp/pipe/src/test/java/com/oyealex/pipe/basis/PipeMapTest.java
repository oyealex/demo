package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的映射类方法的测试。
 *
 * @author oyealex
 * @see Pipe#map(Function)
 * @see Pipe#mapEnumerated(LongBiFunction)
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
    @DisplayName("能够将元素正确地转换为其他类型")
    void should_map_element_to_another_type_rightly() {
        List<Integer> res = of(ELEMENTS).map(String::length).toList();
        assertEquals(stream(ELEMENTS).map(String::length).collect(toList()), res);
    }

    @Test
    @DisplayName("当以支持枚举的方式映射元素时能够正常访问元素的枚举次序")
    void should_access_enumeration_number_rightly_when_map_elements_enumerated() {
        List<String> res = of(ELEMENTS).mapEnumerated((index, value) -> String.valueOf(index)).toList();
        int[] index = new int[1];
        assertEquals(stream(ELEMENTS).map(value -> String.valueOf(index[0]++)).collect(toList()), res);
    }

    @Test
    @DisplayName("能够将元素正确地转换为其他类型的流水线")
    void should_flat_map_element_to_another_type_pipe_rightly() {
        List<Integer> res = of(ELEMENTS).flatMap(value -> Pipes.from(value.chars().iterator())).toList();
        assertEquals(stream(ELEMENTS).flatMapToInt(String::chars).boxed().collect(toList()), res);
    }
}
