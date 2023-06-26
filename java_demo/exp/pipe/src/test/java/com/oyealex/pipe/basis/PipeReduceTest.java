package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipe.empty;
import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.singleton;
import static java.lang.String.join;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线缩减系列API的测试。
 *
 * @author oyealex
 * @see Pipe#reduce(BinaryOperator)
 * @see Pipe#reduce(Object, BiFunction)
 * @see Pipe#reduce(Object, Function, BiFunction)
 * @see Pipe#reduceTo(Object, BiConsumer)
 * @since 2023-06-15
 */
class PipeReduceTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确使用给定的二元操作缩减流水线元素")
    void should_reduce_pipe_elements_by_given_binary_operator_rightly() {
        List<String> sample = genRandomStrList();
        assertAll(() -> assertEquals(Optional.of(SOME_STR), singleton(SOME_STR).reduce((l, r) -> l + r)),
            () -> assertEquals(Optional.of(join("", sample)), list(sample).reduce((l, r) -> l + r)),
            () -> assertEquals(Optional.empty(), Pipe.<String>empty().reduce((l, r) -> l + r)));
    }

    @Test
    @DisplayName("能够正确使用给定的初始值和二元映射方法缩减流水线元素")
    void should_reduce_pipe_elements_by_given_init_value_and_binary_function_rightly() {
        List<String> sample = genRandomStrList();
        List<Object> sameInitValue = new ArrayList<>();
        assertAll(() -> assertEquals(join("", sample),
                list(sample).reduce(new StringBuilder(), StringBuilder::append).toString()),
            () -> assertSame(sameInitValue, list(sample).reduce(sameInitValue, wrapToFunction(List::add))),
            () -> assertSame(sameInitValue, Pipe.<String>empty().reduce(sameInitValue, wrapToFunction(List::add))));
    }

    @Test
    @DisplayName("能够正确使用给定的初始值和二元映射方法缩减映射后的流水线元素")
    void should_reduce_mapped_pipe_elements_by_given_init_value_and_binary_function_rightly() {
        List<String> sample = genRandomStrList();
        List<Object> sameInitValue = new ArrayList<>();
        assertAll(() -> assertEquals(sample.stream().map(String::toUpperCase).collect(toList()),
                list(sample).reduce(new ArrayList<>(), String::toUpperCase, wrapToFunction(List::add))),
            () -> assertEquals(sample.stream().mapToInt(String::length).sum(),
                list(sample).reduce(0, String::length, Integer::sum)),
            () -> assertEquals(sample.stream().map(String::length).collect(toList()),
                list(sample).reduce(new ArrayList<>(), String::length, wrapToFunction(List::add))),
            () -> assertSame(sameInitValue,
                Pipe.<String>empty().reduce(sameInitValue, String::length, wrapToFunction(List::add))));
    }

    @Test
    @DisplayName("能够正确使用给定的结果值和二元访问方法缩减映射后的流水线元素")
    void should_reduce_pipe_elements_by_given_result_value_and_binary_consumer_rightly() {
        List<String> sample = genRandomStrList();
        List<String> sameResult = new ArrayList<>();
        assertAll(() -> assertEquals(sample, list(sample).reduceTo(sameResult, List::add)),
            () -> assertSame(sameResult, list(sample).reduceTo(sameResult, List::add)),
            () -> assertTrue(Pipe.<String>empty().reduceTo(new ArrayList<>(), List::add).isEmpty()));
    }

    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().reduce(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().reduce(new Object(), null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> empty().reduce(new Object(), null, (r, v) -> r)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().reduce(new Object(), identity(), null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().reduceTo(new Object(), null)));
    }
}
