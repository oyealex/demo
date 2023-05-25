package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipes.list;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PipeDropTest
 *
 * @author oyealex
 * @see Pipe#dropIf(Predicate)
 * @see Pipe#dropIfOrderly(LongBiPredicate)
 * @see Pipe#dropFirst()
 * @see Pipe#dropFirst(int)
 * @see Pipe#dropLast()
 * @see Pipe#dropLast(int)
 * @see Pipe#dropWhile(Predicate)
 * @see Pipe#dropWhileOrderly(LongBiPredicate)
 * @see Pipe#dropNull()
 * @see Pipe#dropNullBy(Function)
 * @since 2023-05-26
 */
public class PipeDropTest extends PipeTestFixture {
    // normal test

    @Test
    @DisplayName("能够正确根据断言丢弃元素")
    void should_drop_elements_as_predicate_rightly() {
        List<String> sample = generateIntegerStrList();
        Predicate<String> predicate = val -> val.length() > 5;
        assertEquals(sample.stream().filter(predicate.negate()).collect(toList()),
            list(sample).dropIf(predicate).toList());
    }

    @Test
    @DisplayName("能够正确根据有序断言丢弃元素")
    void should_drop_elements_as_predicate_with_order_rightly() {
        List<String> sample = generateIntegerStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().filter(ignored -> (counter.getAndIncrement() & 1) != 1).collect(toList()),
            list(sample).dropIfOrderly((order, value) -> (order & 1) == 1).toList());
    }

    @Test
    @DisplayName("在非空流水线中能正丢弃第一个元素")
    void should_drop_first_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(1, sample.size()), list(sample).dropFirst().toList());
    }

    @Test
    @DisplayName("在空流水线中尝试丢弃第一个元素得到空流水线")
    void should_get_empty_pipe_when_try_to_drop_first_element_in_empty_pipe() {
        assertEquals(emptyList(), Pipes.empty().dropFirst().toList());
    }

    @Test
    @DisplayName("当尝试丢弃的前N个元素数量小于流水线元素数量时，能够正丢弃前N个元素")
    void should_get_elements_except_first_N_when_try_drop_first_N_elements_and_N_is_smaller_than_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample.subList(5, sample.size()), list(sample).dropFirst(5).toList());
    }

    @Test
    @DisplayName("当尝试丢弃的前N个元素数量大于等于流水线元素数量时，能够正丢弃得全部元素")
    void should_get_empty_pipe_when_try_drop_first_N_elements_and_N_is_equal_to_or_bigger_than_the_pipe_size() {
        assertEquals(emptyList(), infiniteIntegerStrPipe().limit(10).dropFirst(10).toList());
        assertEquals(emptyList(), infiniteIntegerStrPipe().limit(10).dropFirst(20).toList());
    }

    @Test
    @DisplayName("当尝试丢弃前0个元素时得到所有元素")
    void should_get_all_elements_when_try_to_get_first_zero_elements() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample, list(sample).dropFirst(0).toList());
    }

    @Test
    @DisplayName("在非空流水线中能够正丢弃取最后一个元素")
    void should_drop_last_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(0, sample.size() - 1), list(sample).dropLast().toList());
    }

    @Test
    @DisplayName("在空流水线中尝试丢弃最后一个元素得到空流水线")
    void should_get_empty_pipe_when_try_to_get_last_element_in_empty_pipe() {
        assertEquals(emptyList(), Pipes.empty().dropLast().toList());
    }

    @Test
    @DisplayName("当尝试丢弃的最后N个元素数量小于流水线元素数量时，能够正丢弃最后N个元素")
    void should_get_elements_except_last_N_when_try_drop_last_N_elements_and_N_is_smaller_than_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample.subList(0, 5), list(sample).dropLast(5).toList());
    }

    @Test
    @DisplayName("当尝试丢弃的最后N个元素数量大于等于流水线元素数量时，得到空流水线")
    void should_get_no_elements_when_try_drop_last_N_elements_and_N_is_equal_to_or_bigger_than_the_pipe_size() {
        assertEquals(emptyList(), infiniteIntegerStrPipe().limit(10).dropLast(10).toList());
        assertEquals(emptyList(), infiniteIntegerStrPipe().limit(10).dropLast(20).toList());
    }

    @Test
    @DisplayName("当尝试丢弃最后0个元素时得到所有元素")
    void should_get_all_elements_when_try_to_get_last_zero_elements() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample, list(sample).dropLast(0).toList());
    }

    @Test
    @DisplayName("dropWhile能够持续丢弃元素，直到断言首次为false")
    void should_drop_elements_until_predicate_get_false_firstly_when_drop_while() {
        IntBox counter = IntBox.box();
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(5, sample.size()),
            list(sample).dropWhile(str -> counter.getAndIncrement() < 5).toList());
    }

    @Test
    @DisplayName("能够正确根据有序断言丢弃元素直到断言首次为false")
    void should_drop_elements_as_predicate_with_order_rightly_when_drop_while() {
        List<String> sample = generateIntegerStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().filter(ignored -> counter.getAndIncrement() >= 5).collect(toList()),
            list(sample).dropWhileOrderly((order, value) -> order < 5).toList());
    }

    @Test
    @DisplayName("能够正确丢弃空元素")
    void should_drop_null_elements_rightly() {
        List<String> sample = generateOddIntegerStrWithNullsList();
        assertTrue(sample.stream().anyMatch(Objects::isNull));
        assertTrue(list(sample).dropNull().toList().stream().noneMatch(Objects::isNull));
    }

    @Test
    @DisplayName("能够根据映射结果正确丢弃空元素")
    void should_drop_null_elements_by_mapped_value_rightly() {
        List<Integer> sample = generateIntegerList();
        Function<Integer, String> mapper = value -> (value & 1) == 1 ? null : "";
        assertEquals(sample.stream().filter(value -> mapper.apply(value) != null).collect(toList()),
            list(sample).dropNullBy(mapper).toList());
    }

    // exception test

    @Test
    @DisplayName("当给定的断言方法为空时抛出异常")
    void should_throw_exception_when_predicate_is_null() {
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().dropIf(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().dropIfOrderly(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().dropWhile(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().dropWhileOrderly(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().dropNullBy(null));
    }

    @Test
    @DisplayName("当尝试丢弃的元素数量为负值时抛出异常")
    void should_throw_exception_when_given_count_is_negative() {
        assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerStrPipe().dropFirst(-1));
        assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerStrPipe().dropLast(-1));
    }
}
