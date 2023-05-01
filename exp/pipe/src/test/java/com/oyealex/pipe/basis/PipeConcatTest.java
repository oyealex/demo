package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的拼接类方法的测试。
 *
 * @author oyealex
 * @see Pipe#prepend(Iterator)
 * @see Pipe#prepend(Iterable)
 * @see Pipe#prepend(Pipe)
 * @see Pipe#prepend(Stream)
 * @see Pipe#prepend(Object[])
 * @see Pipe#append(Iterator)
 * @see Pipe#append(Iterable)
 * @see Pipe#append(Pipe)
 * @see Pipe#append(Stream)
 * @see Pipe#append(Object[])
 * @since 2023-05-01
 */
class PipeConcatTest extends PipeTestBase {
    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线头部")
    void should_prepend_elements_from_iterator_into_head_of_pipe_rightly() {
        List<String> res = of(ELEMENTS).prepend(asList(OTHER_ELEMENTS).iterator()).toList();
        assertEquals(Stream.concat(stream(OTHER_ELEMENTS), stream(ELEMENTS)).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确地拼接迭代器中地元素到流水线尾部")
    void should_append_elements_from_iterator_into_tail_of_pipe_rightly() {
        List<String> res = of(ELEMENTS).append(asList(OTHER_ELEMENTS).iterator()).toList();
        assertEquals(Stream.concat(stream(ELEMENTS), stream(OTHER_ELEMENTS)).collect(toList()), res);
    }
}
