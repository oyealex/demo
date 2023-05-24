package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
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

/**
 * PipeTakeTest
 *
 * @author oyealex
 * @see Pipe#takeIf(Predicate)
 * @see Pipe#takeIfOrderly(LongBiPredicate)
 * @see Pipe#takeFirst()
 * @see Pipe#takeLast()
 * @see Pipe#takeLast(int)
 * @see Pipe#takeWhile(Predicate)
 * @see Pipe#takeWhileOrderly(LongBiPredicate)
 * @since 2023-05-25
 */
class PipeTakeTest extends PipeTestBase {
    @Test
    @DisplayName("能够正确根据断言保留元素")
    void should_take_elements_as_predicate_rightly() {
        List<String> sample = generateRandStrList();
        Predicate<String> predicate = val -> val.length() > 5;
        assertEquals(sample.stream().filter(predicate).collect(toList()), list(sample).takeIf(predicate).toList());
    }

    @Test
    @DisplayName("能够正确根据有序断言保留元素")
    void should_take_elements_as_predicate_with_order_rightly() {
        List<String> sample = generateRandStrList();
        IntBox counter = IntBox.box();
        assertEquals(sample.stream().filter(ignored -> (counter.getAndDecrement() & 1) == 1).collect(toList()),
            list(sample).takeIfOrderly((order, value) -> (order & 1) == 1).toList());
    }

    @Test
    @DisplayName("在非空流水线中能正确获取第一个元素")
    void should_take_first_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateRandStrList();
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
        List<String> sample = generateRandStrList();
        List<String> handledElementsAfterTaking = new ArrayList<>();
        List<String> firstElement = list(sample).peek(handledElementsAfterTaking::add).takeFirst().toList();
        assertEquals(firstElement, handledElementsAfterTaking);
        assertEquals(singletonList(sample.get(0)), firstElement);
    }

    @Test
    @DisplayName("在非空流水线中能够正确获取最后一个元素")
    void should_take_last_element_in_non_empty_pipe_rightly() {
        List<String> sample = generateRandStrList();
        assertEquals(sample.subList(sample.size() - 1, sample.size()), list(sample).takeLast().toList());
    }

    @Test
    @DisplayName("在空流水线中尝试获取最后一个元素得到空流水线")
    void should_get_empty_pipe_when_try_to_get_last_element_in_empty_pipe() {
        assertEquals(emptyList(), Pipes.empty().takeLast().toList());
    }
}
