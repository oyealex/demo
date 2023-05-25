package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipes.list;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * PipeTakeTest
 *
 * @author oyealex
 * @see Pipe#takeIf(Predicate)
 * @see Pipe#filter(Predicate)
 * @see Pipe#takeIfOrderly(LongBiPredicate)
 * @see Pipe#takeFirst()
 * @see Pipe#takeFirst(int)
 * @see Pipe#takeLast()
 * @see Pipe#takeLast(int)
 * @see Pipe#takeWhile(Predicate)
 * @see Pipe#takeWhileOrderly(LongBiPredicate)
 * @since 2023-05-25
 */
class PipeTakeTest extends PipeTestBase {
    // normal test

    @Test
    @DisplayName("能够正确根据断言保留元素")
    void should_take_elements_as_predicate_rightly() {
        List<String> sample = generateIntegerStrList();
        Predicate<String> predicate = val -> val.length() > 5;
        assertEquals(sample.stream().filter(predicate).collect(toList()), list(sample).takeIf(predicate).toList());
    }

    @Test
    @DisplayName("能够正确根据断言过滤元素")
    void should_filter_elements_as_predicate_rightly() {
        List<String> sample = generateIntegerStrList();
        Predicate<String> predicate = val -> val.length() > 5;
        assertEquals(sample.stream().filter(predicate).collect(toList()), list(sample).filter(predicate).toList());
    }

    @Test
    @DisplayName("能够正确根据有序断言保留元素")
    void should_take_elements_as_predicate_with_order_rightly() {
        List<String> sample = generateIntegerStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().filter(ignored -> (counter.getAndIncrement() & 1) == 1).collect(toList()),
            list(sample).takeIfOrderly((order, value) -> (order & 1) == 1).toList());
    }

    @Test
    @DisplayName("在非空流水线中能正确获取第一个元素")
    void should_take_first_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(0, 1), list(sample).takeFirst().toList());
    }

    @Test
    @DisplayName("在空流水线中尝试获取第一个元素得到空流水线")
    void should_get_empty_pipe_when_try_to_get_first_element_in_empty_pipe() {
        assertEquals(emptyList(), Pipes.empty().takeFirst().toList());
    }

    @Test
    @DisplayName("获取第一个元素之后应当短路流水线")
    void should_circuit_after_taking_first_element() {
        List<String> sample = generateIntegerStrList();
        List<String> handled = new ArrayList<>();
        List<String> first = list(sample).peek(handled::add).takeFirst().toList();
        assertEquals(first, handled);
        assertEquals(singletonList(sample.get(0)), first);
    }

    @Test
    @DisplayName("当尝试获取的前N个元素数量小于流水线元素数量时，能够正确获得前N个元素")
    void should_get_first_N_elements_when_try_take_first_N_elements_and_N_is_smaller_then_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample.subList(0, 5), list(sample).takeFirst(5).toList());
    }

    @Test
    @DisplayName("当尝试获取的前N个元素数量大于等于流水线元素数量时，能够正确获得全部元素")
    void should_get_first_N_elements_when_try_take_first_N_elements_and_N_is_equal_to_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample, list(sample).takeFirst(10).toList());
        assertEquals(sample, list(sample).takeFirst(20).toList());
    }

    @Test
    @DisplayName("当尝试获取前0个元素时得到空元素")
    void should_get_no_elements_when_try_to_get_first_zero_elements() {
        assertEquals(emptyList(), infiniteIntegerStrPipe().takeFirst(0).toList());
    }

    @Test
    @DisplayName("获取前N个元素之后应当短路流水线，N小于流水线元素数量")
    void should_circuit_after_taking_first_N_element_when_N_is_smaller_then_pipe_size() {
        List<String> handled = new ArrayList<>();
        List<String> firstN = infiniteIntegerStrPipe().limit(10).peek(handled::add).takeFirst(5).toList();
        assertEquals(firstN, handled);
    }

    @Test
    @DisplayName("在非空流水线中能够正确获取最后一个元素")
    void should_take_last_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(sample.size() - 1, sample.size()), list(sample).takeLast().toList());
    }

    @Test
    @DisplayName("在空流水线中尝试获取最后一个元素得到空流水线")
    void should_get_empty_pipe_when_try_to_get_last_element_in_empty_pipe() {
        assertEquals(emptyList(), Pipes.empty().takeLast().toList());
    }

    @Test
    @DisplayName("当尝试获取的最后N个元素数量小于流水线元素数量时，能够正确获得最后N个元素")
    void should_get_last_N_elements_when_try_take_last_N_elements_and_N_is_smaller_then_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample.subList(5, 10), list(sample).takeLast(5).toList());
    }

    @Test
    @DisplayName("当尝试获取的最后N个元素数量大于等于流水线元素数量时，能够正确获得全部元素")
    void should_get_last_N_elements_when_try_take_last_N_elements_and_N_is_equal_to_the_pipe_size() {
        List<String> sample = infiniteIntegerStrPipe().limit(10).toList();
        assertEquals(sample, list(sample).takeLast(10).toList());
        assertEquals(sample, list(sample).takeLast(20).toList());
    }

    @Test
    @DisplayName("当尝试获取最后0个元素时得到空元素")
    void should_get_no_elements_when_try_to_get_last_zero_elements() {
        assertEquals(emptyList(), infiniteIntegerStrPipe().takeLast(0).toList());
    }

    @Test
    @DisplayName("takeWhile能够持续获取元素，直到断言首次为false")
    void should_take_elements_until_predicate_get_false_firstly_when_take_while() {
        IntBox counter = IntBox.box();
        List<String> sample = generateIntegerStrList();
        assertEquals(sample.subList(0, 5), list(sample).takeWhile(str -> counter.getAndIncrement() < 5).toList());
    }

    @Test
    @DisplayName("当takeWhile的断言首次为false后应当短路流水线")
    void should_circuit_after_predicate_get_false_when_take_while() {
        IntBox counter = IntBox.box();
        List<String> sample = generateIntegerStrList();
        List<String> handled = new ArrayList<>();
        list(sample).peek(handled::add).takeWhile(str -> counter.getAndIncrement() < 5).run();
        assertEquals(sample.subList(0, 6), handled);
    }

    @Test
    @DisplayName("能够正确根据有序断言保留元素直到断言首次为false")
    void should_take_elements_as_predicate_with_order_rightly_when_take_while() {
        List<String> sample = generateIntegerStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().filter(ignored -> counter.getAndIncrement() < 5).collect(toList()),
            list(sample).takeWhileOrderly((order, value) -> order < 5).toList());
    }

    // exception test

    @Test
    @DisplayName("当给定的断言方法为空时抛出异常")
    void should_throw_exception_when_predicate_is_null() {
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().takeIf(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().filter(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().takeIfOrderly(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().takeWhile(null));
        assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerStrPipe().takeWhileOrderly(null));
    }

    @Test
    @DisplayName("当尝试获取的元素数量为负值时抛出异常")
    void should_throw_exception_when_given_count_is_negative() {
        assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerStrPipe().takeFirst(-1));
        assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerStrPipe().takeLast(-1));
    }

    @Test
    @DisplayName("当尝试获取无限流中的最后N个元素时抛出异常")
    @Disabled("特性尚未实现")
    void should_throw_exception_when_try_to_take_last_N_elements_in_infinite_pipe() {
        assertThrowsExactly(IllegalStateException.class, () -> infiniteIntegerStrPipe().takeLast());
        assertThrowsExactly(IllegalStateException.class, () -> infiniteIntegerStrPipe().takeLast(10));
    }
}
