package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.Box;
import com.oyealex.pipe.assist.LongBox;
import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.functional.LongBiConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.oyealex.pipe.assist.Tuple.of;
import static com.oyealex.pipe.basis.Pipe.empty;
import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线其他杂项API的测试。
 *
 * @author oyealex
 * @see Pipe#peek(Consumer)
 * @see Pipe#peekOrderly(LongBiConsumer)
 * @see Pipe#forEach(Consumer)
 * @see Pipe#forEachOrderly(LongBiConsumer)
 * @see Pipe#run()
 * @see Pipe#count()
 * @see Pipe#chain(Function)
 * @see Pipe#close()
 * @since 2023-05-30
 */
class PipeMiscTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确对每个元素执行访问方法")
    void should_peek_every_elements_rightly() {
        List<String> sample = genRandomStrList();
        List<String> peeked = new ArrayList<>();
        list(sample).peek(peeked::add).run();
        assertEquals(sample, peeked);
    }

    @Test
    @DisplayName("能够正确对每个元素执行终结访问方法")
    void should_visit_every_elements_rightly() {
        List<String> sample = genRandomStrList();
        List<String> visited = new ArrayList<>();
        list(sample).forEach(visited::add);
        assertEquals(sample, visited);
    }

    @Test
    @DisplayName("能够正确对每个元素依次执行访问方法")
    void should_peek_every_elements_in_order_rightly() {
        List<String> sample = genRandomStrList();
        List<Tuple<Long, String>> peeked = new ArrayList<>();
        list(sample).peekOrderly((order, value) -> peeked.add(of(order, value))).run();
        LongBox counter = LongBox.box();
        assertEquals(sample.stream().map(value -> of(counter.getAndIncrement(), value)).collect(toList()), peeked);
    }

    @Test
    @DisplayName("能够正确对每个元素依次执行终结访问方法")
    void should_visit_every_elements_in_order_rightly() {
        List<String> sample = genRandomStrList();
        List<Tuple<Long, String>> visited = new ArrayList<>();
        list(sample).forEachOrderly((order, value) -> visited.add(of(order, value)));
        LongBox counter = LongBox.box();
        assertEquals(sample.stream().map(value -> of(counter.getAndIncrement(), value)).collect(toList()), visited);
    }

    @Test
    @DisplayName("能够正确计数流水线元素")
    void should_count_elements_rightly() {
        assertAll(() -> assertEquals(10, infiniteIntegerPipe().limit(10).count()),
            () -> assertEquals(0, empty().count()));
    }

    @Test
    @DisplayName("能够正确执行链式方法")
    void should_execute_chain_method_rightly() {
        List<String> sample = genRandomStrList();
        assertEquals(list(sample).sort().toList(), list(sample).chain(Pipe::sort).toList());
    }

    @Test
    @DisplayName("当关闭流水线时能够正确执行关闭动作")
    void should_execute_close_action_when_close_pipe() {
        Box<Boolean> mark = Box.box(false);
        try (Pipe<String> pipe = Pipe.<String>empty().onClose(() -> mark.setValue(true))) {
            pipe.run();
        }
        assertTrue(mark.get());
    }

    @Test
    @DisplayName("当关闭流水线时能够正确执行关闭动作，即使关闭动作会抛出异常")
    void should_execute_close_action_when_close_pipe_even_the_action_will_throw_exception() {
        Box<Boolean> mark = Box.box(false);
        assertThrowsExactly(IllegalStateException.class, () -> {
            try (Pipe<String> pipe = Pipe.<String>empty().onClose(() -> {
                throw new IllegalStateException();
            }).onClose(() -> mark.setValue(true))) {
                pipe.run();
            }
        });
        assertTrue(mark.get());
    }

    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().peek(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().peekOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().forEach(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().forEachOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().chain(null)));
    }
}
