package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.functional.LongBiFunction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线{@code flatMap}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#flatMap(Function)
 * @see Pipe#flatMapOrderly(LongBiFunction)
 * @see Pipe#flatMapCollection(Function)
 * @see Pipe#flatMapSingleton()
 * @see Pipe#flatMapToInt(Function)
 * @see Pipe#flatMapToIntOrderly(LongBiFunction)
 * @see Pipe#flatMapToLong(Function)
 * @see Pipe#flatMapToLongOrderly(LongBiFunction)
 * @see Pipe#flatMapToDouble(Function)
 * @see Pipe#flatMapToDoubleOrderly(LongBiFunction)
 * @since 2023-05-27
 */
class PipeFlatMapTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确扁平映射元素")
    void should_flat_map_elements_rightly() {
        List<Integer> sample = generateIntegerList();
        assertEquals(
            sample.stream().map(PipeTestFixture::generateIntegerStrList).flatMap(Collection::stream).collect(toList()),
            list(sample)
                .map(PipeTestFixture::generateIntegerStrList)
                .flatMap(list -> list((List<? extends String>) list))
                .toList());
    }

    @Test
    @DisplayName("能够根据次序正确扁平映射元素")
    void should_flat_map_elements_orderly_rightly() {
        List<String> sample = generateIntegerStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream()
            .map(ignored -> generateIntegerStrList(counter.getAndIncrement()))
            .flatMap(Collection::stream)
            .collect(toList()), list(sample).flatMapOrderly((order, value) -> {
            List<? extends String> list = generateIntegerStrList((int) order);
            return list(list);
        }).toList());
    }

    @Test
    @DisplayName("能够将元素映射为容器，并正确地进行扁平映射")
    void should_flat_map_collection_elements_rightly() {
        List<Integer> sample = generateIntegerList();
        assertEquals(
            sample.stream().map(PipeTestFixture::generateIntegerStrList).flatMap(Collection::stream).collect(toList()),
            list(sample).flatMapCollection(PipeTestFixture::generateIntegerStrList).toList());
    }

    @Test
    @DisplayName("能正确将元素扁平映射为单例流水线")
    void should_flat_map_to_singleton_pipe_rightly() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.stream().map(Stream::of).map(stream -> stream.collect(toList())).collect(toList()),
            list(sample).flatMapSingleton().map(Pipe::toList).toList());
    }

    @Test
    @DisplayName("能正确将元素扁平映射为基础类型流水线")
    @Disabled("扁平映射为基础类型的流水线API尚未实现")
    void should_flat_map_to_pipe_with_primitive_element_type() {}

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().flatMap(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().flatMapOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().flatMapCollection(null)));
    }
}
