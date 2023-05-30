package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.LongBox;
import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipes.list;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线其他杂项API的测试。
 *
 * @author oyealex
 * @see Pipe#reverse()
 * @see Pipe#shuffle()
 * @see Pipe#shuffle(Random)
 * @see Pipe#peek(Consumer)
 * @see Pipe#peekOrderly(LongBiConsumer)
 * @since 2023-05-30
 */
class PipeMiscTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确的逆序元素")
    void should_reverse_elements_rightly() {
        List<String> sample = generateRandomStrList();
        List<String> expected = new ArrayList<>(sample);
        Collections.reverse(expected);
        assertEquals(expected, list(sample).reverse().toList());
    }

    @Test
    @DisplayName("逆序一个已经自然有序的流水线等同于将流水线逆序排序")
    void should_sort_in_reversed_natural_order_when_reverse_a_naturally_sorted_pipe() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(reverseOrder()).collect(toList()), list(sample).sort().reverse().toList());
    }

    @Test
    @DisplayName("逆序一个已经自然有序的流水线等同于将流水线逆序排序")
    void should_sort_in_natural_order_when_reverse_a_pipe_in_reversed_natural_order() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted().collect(toList()), list(sample).sortReversely().reverse().toList());
    }

    @Test
    @DisplayName("能够正确打乱元素顺序")
    void should_shuffle_elements_in_different_order() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertNotEquals(sample, list(sample).shuffle().toList()),
            () -> assertNotEquals(list(sample).shuffle().toList(), list(sample).shuffle().toList()),
            () -> assertNotEquals(list(sample).shuffle().toList(), list(sample).shuffle().toList()),
            () -> assertNotEquals(list(sample).shuffle().toList(), list(sample).shuffle().toList()));
    }

    @Test
    @DisplayName("能够根据给定的随机数正确打乱元素顺序")
    void should_shuffle_elements_by_the_given_random() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertNotEquals(sample, list(sample).shuffle(new Random(0L)).toList()),
            () -> assertEquals(list(sample).shuffle(new Random(0L)).toList(),
                list(sample).shuffle(new Random(0L)).toList()),
            () -> assertEquals(list(sample).shuffle(new Random(0L)).toList(),
                list(sample).shuffle(new Random(0L)).toList()),
            () -> assertEquals(list(sample).shuffle(new Random(0L)).toList(),
                list(sample).shuffle(new Random(0L)).toList()));
    }

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

    @Test
    @DisplayName("如果一个流水线已经自然有序或自然逆序，逆序之后执行任意自然有序或自然逆序排序不会真正执行比较")
    void should_not_do_sort_actually_when_reverse_a_pipe_which_is_already_sorted() {
        assertAll(() -> testForReverseOptimization(Pipe::sort, Pipe::sort),
            () -> testForReverseOptimization(Pipe::sort, Pipe::sortReversely),
            () -> testForReverseOptimization(Pipe::sortReversely, Pipe::sort),
            () -> testForReverseOptimization(Pipe::sortReversely, Pipe::sortReversely));
    }

    private void testForReverseOptimization(Function<Pipe<ComparableTestDouble>, Pipe<ComparableTestDouble>> prepare,
        Function<Pipe<ComparableTestDouble>, Pipe<ComparableTestDouble>> sort) {
        assertTrue(infiniteRandomStrPipe().limit(NORMAL_SIZE)
            .map(ComparableTestDouble::new)
            .chain(prepare)
            .reverse()
            .peek(ComparableTestDouble::reset) // 重置记录
            .chain(sort)
            .toList()
            .stream()
            .noneMatch(ComparableTestDouble::isCompareToCalled));
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().shuffle(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().peek(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().peekOrderly(null)));
    }
}
