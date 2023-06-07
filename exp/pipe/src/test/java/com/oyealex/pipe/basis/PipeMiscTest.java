package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.LongBox;
import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.functional.LongBiConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线其他杂项API的测试。
 *
 * @author oyealex
 * @see Pipe#peek(Consumer)
 * @see Pipe#peekOrderly(LongBiConsumer)
 * @since 2023-05-30
 */
class PipeMiscTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确对每个元素执行访问方法")
    void should_peek_every_elements_rightly() {
        List<String> sample = generateRandomStrList();
        List<String> peeked = new ArrayList<>();
        list(sample).peek(peeked::add).run();
        assertEquals(sample, peeked);
    }

    @Test
    @DisplayName("能够正确对每个元素依次执行访问方法")
    void should_peek_every_elements_in_order_rightly() {
        List<String> sample = generateRandomStrList();
        List<Tuple<Long, String>> peeked = new ArrayList<>();
        list(sample).peekOrderly((order, value) -> peeked.add(Tuple.of(order, value))).run();
        LongBox counter = LongBox.box();
        assertEquals(sample.stream().map(value -> Tuple.of(counter.getAndIncrement(), value)).collect(toList()),
            peeked);
    }

    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().peek(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().peekOrderly(null)));
    }
}
