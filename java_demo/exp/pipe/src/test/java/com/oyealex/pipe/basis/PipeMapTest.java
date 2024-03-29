package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线{@code map}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#map(Function)
 * @see Pipe#mapOrderly(LongBiFunction)
 * @see Pipe#mapIf(Predicate, Object)
 * @see Pipe#mapIf(Predicate, Supplier)
 * @see Pipe#mapIf(Predicate, Function)
 * @see Pipe#mapIf(Function)
 * @see Pipe#mapToString()
 * @see Pipe#mapToString(String)
 * @see Pipe#mapIfNull(Object)
 * @see Pipe#mapIfNull(Supplier)
 * @see Pipe#mapIfNonNull(Function)
 * @since 2023-04-28
 */
class PipeMapTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确映射元素")
    void should_map_elements_rightly() {
        List<Integer> sample = genIntegerList();
        assertEquals(sample.stream().map(String::valueOf).collect(toList()),
            list(sample).map(String::valueOf).toList());
    }

    @Test
    @DisplayName("能够正确映射元素为null")
    void should_map_to_null_rightly() {
        assertTrue(infiniteIntegerPipe().limit(10).map(ignored -> null).toList().stream().allMatch(Objects::isNull));
    }

    @Test
    @DisplayName("能够正确根据次序映射元素")
    void should_map_orderly_rightly() {
        List<Integer> sample = genIntegerList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().map(value -> counter.getAndIncrement() + "," + value).collect(toList()),
            list(sample).mapOrderly((order, value) -> order + "," + value).toList());
    }

    @Test
    @DisplayName("能够正确根据条件映射元素为给定值")
    void should_map_to_given_value_as_condition_rightly() {
        List<Integer> sample = genIntegerList();
        assertEquals(sample.stream().map(value -> isOdd(value) ? 0 : value).collect(toList()),
            list(sample).mapIf(PipeTestFixture::isOdd, 0).toList());
    }

    @Test
    @DisplayName("能够正确根据条件映射为给定supplier的结果")
    void should_map_to_given_supplier_result_as_condition_rightly() {
        List<Integer> sample = genIntegerList();
        IntBox seed = IntBox.box();
        IntBox seed2 = IntBox.box();
        assertEquals(sample.stream().map(value -> isOdd(value) ? seed.getAndIncrement() : value).collect(toList()),
            list(sample).mapIf(PipeTestFixture::isOdd, seed2::getAndIncrement).toList());
    }

    @Test
    @DisplayName("能够正确根据条件映射元素为给定方法的映射值")
    void should_map_to_given_mapper_value_as_condition_rightly() {
        List<Integer> sample = genIntegerList();
        assertEquals(
            sample.stream().map(value -> isOdd(value) ? Integer.numberOfLeadingZeros(value) : value).collect(toList()),
            list(sample).mapIf(PipeTestFixture::isOdd, Integer::numberOfLeadingZeros).toList());
    }

    @Test
    @DisplayName("能够根据给定方法映射结果的Optional情况映射对应的元素")
    void should_map_to_given_mapper_value_as_optional_result_rightly() {
        List<Integer> sample = genIntegerList();
        Function<Integer, Optional<Integer>> mapper = value -> isOdd(value) ? Optional.of(-value) : Optional.empty();
        assertEquals(sample.stream().map(value -> mapper.apply(value).orElse(value)).collect(toList()),
            list(sample).mapIf(mapper).toList());
    }

    @Test
    @DisplayName("能够正确映射元素为字符串")
    void should_map_to_string_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        assertEquals(sample.stream().map(Objects::toString).collect(toList()), list(sample).mapToString().toList());
    }

    @Test
    @DisplayName("能够正确映射元素为字符串，并将null值映射为给定的默认字符串")
    void should_map_to_string_with_default_value_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        assertEquals(sample.stream().map(val -> Objects.toString(val, SOME_STR)).collect(toList()),
            list(sample).mapToString(SOME_STR).toList());
    }

    @Test
    @DisplayName("能够正确将null值映射为给定值")
    void should_map_null_to_given_value_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        assertEquals(sample.stream().map(value -> value == null ? SOME_STR : value).collect(toList()),
            list(sample).mapIfNull(SOME_STR).toList());
    }

    @Test
    @DisplayName("能够正确将null值映射为给定supplier结果值")
    void should_map_null_to_given_supplier_result_value_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        assertEquals(sample.stream().map(value -> value == null ? SOME_STR : value).collect(toList()),
            list(sample).mapIfNull(() -> SOME_STR).toList());
    }

    @Test
    @DisplayName("能够正确使用给定的映射方法将非null的元素映射为其他值")
    void should_map_non_null_by_given_mapper_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        assertEquals(sample.stream().map(v -> v == null ? null : Integer.valueOf(v)).collect(toList()),
            list(sample).mapIfNonNull(Integer::valueOf).toList());
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().map(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().mapOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().mapIf(null, "")),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().mapIf(null, () -> "")),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().mapIf(ignored -> true, (Supplier<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().mapIf(null, val -> val + SOME_STR)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().mapIf(ignored -> true, (Function<? super String, String>) null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().mapIf(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().mapIfNull((Supplier<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().mapIfNull((String) null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().mapIfNonNull(null)));
    }

    @Test
    @DisplayName("当mapNull使用的supplier返回null时，运行中的流水线会抛出异常")
    void should_throw_exception_while_pipe_is_running_when_map_null_with_supplier_which_return_null() {
        assertThrowsExactly(NullPointerException.class,
            () -> infiniteOddIntegerStrWithNullsPipe().mapIfNull(() -> null).run());
    }
}
