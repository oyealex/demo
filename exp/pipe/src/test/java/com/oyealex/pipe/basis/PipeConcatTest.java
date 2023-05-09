package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.api.Pipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.stream;
import static java.util.Spliterators.spliterator;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的拼接类方法的测试。
 *
 * @author oyealex
 * @see Pipe#prepend(Spliterator)
 * @see Pipe#prepend(Iterator)
 * @see Pipe#prepend(Pipe)
 * @see Pipe#prepend(Stream)
 * @see Pipe#prepend(Object[])
 * @see Pipe#prependKeys(Map)
 * @see Pipe#prependValues(Map)
 * @see Pipe#append(Spliterator)
 * @see Pipe#append(Iterator)
 * @see Pipe#append(Pipe)
 * @see Pipe#append(Stream)
 * @see Pipe#append(Object[])
 * @see Pipe#appendKeys(Map)
 * @see Pipe#appendValues(Map)
 * @since 2023-05-01
 */
class PipeConcatTest extends PipeTestBase {
    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线头部")
    void should_prepend_elements_from_iterator_into_head_of_pipe_rightly() {
        List<String> res = of(ELEMENTS).prepend(spliterator(OTHER_ELEMENTS, 0)).toList();
        assertEquals(concat(stream(OTHER_ELEMENTS), stream(ELEMENTS)).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线尾部")
    void should_append_elements_from_iterator_into_tail_of_pipe_rightly() {
        List<String> res = of(ELEMENTS).peek(value -> {}).append(spliterator(OTHER_ELEMENTS, 0)).toList();
        assertEquals(concat(stream(ELEMENTS), stream(OTHER_ELEMENTS)).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线头部，原流水线经过了map操作")
    void should_prepend_elements_from_iterator_into_head_of_pipe_rightly_with_map_operation_in_current_pipe() {
        List<String> res = of(ELEMENTS).map(value -> value.repeat(2)).prepend(spliterator(OTHER_ELEMENTS, 0)).toList();
        assertEquals(concat(stream(OTHER_ELEMENTS), stream(ELEMENTS).map(value -> value.repeat(2))).collect(toList()),
            res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线尾部，原流水线经过了map操作")
    void should_append_elements_from_iterator_into_tail_of_pipe_rightly_with_map_operation_in_current_pipe() {
        List<String> res = of(ELEMENTS).peek(value -> {}).map(value -> value.repeat(2))
            .append(spliterator(OTHER_ELEMENTS, 0)).toList();
        assertEquals(concat(stream(ELEMENTS).map(value -> value.repeat(2)), stream(OTHER_ELEMENTS)).collect(toList()),
            res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线头部，新流水线经过了limit操作")
    void should_prepend_elements_from_iterator_into_head_of_pipe_rightly_with_limit_in_new_pipe() {
        List<String> res = of(ELEMENTS).prepend(spliterator(OTHER_ELEMENTS, 0)).limit(OTHER_ELEMENTS.length + 3)
            .toList();
        assertEquals(
            concat(stream(OTHER_ELEMENTS), stream(ELEMENTS)).limit(OTHER_ELEMENTS.length + 3).collect(toList()),
            res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线尾部，新流水线经过了limit操作")
    void should_append_elements_from_iterator_into_tail_of_pipe_rightly_with_limit_in_new_pipe() {
        List<String> res = of(ELEMENTS).peek(value -> {}).append(spliterator(OTHER_ELEMENTS, 0))
            .limit(ELEMENTS.length + 3).toList();
        assertEquals(concat(stream(ELEMENTS), stream(OTHER_ELEMENTS)).limit(ELEMENTS.length + 3).collect(toList()),
            res);
    }
}
