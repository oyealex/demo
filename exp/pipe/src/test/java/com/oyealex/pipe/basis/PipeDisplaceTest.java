package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.Box;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线中元素位移系列API的测试。
 *
 * @author oyealex
 * @see Pipe#selectToFirst(Predicate)
 * @see Pipe#selectToLast(Predicate)
 * @see Pipe#selectNullsToFirst()
 * @see Pipe#selectNullsToFirstBy(Function)
 * @see Pipe#selectNullsToLast()
 * @see Pipe#selectNullsToLastBy(Function)
 * @see Pipe#reverse()
 * @see Pipe#shuffle()
 * @see Pipe#shuffle(Random)
 * @since 2023-06-01
 */
class PipeDisplaceTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确将选中的元素移动到流水线头部或尾部")
    void should_move_selected_elements_to_the_head_or_tail_of_pipe_rightly() {
        List<Integer> sample = generateIntegerList();
        assertAll(() -> assertEquals(moveSelected(sample, PipeTestFixture::isOdd, true),
                list(sample).selectToFirst(PipeTestFixture::isOdd).toList()),
            () -> assertEquals(moveSelected(sample, PipeTestFixture::isEven, false),
                list(sample).selectToLast(PipeTestFixture::isEven).toList()));
    }

    @Test
    @DisplayName("能够正确将流水线中null移动到流水线头部或尾部")
    void should_move_nulls_to_the_head_or_tail_of_pipe_rightly() {
        List<String> sample = generateOddIntegerStrWithNullsList();
        assertAll(() -> assertEquals(moveSelected(sample, Objects::isNull, true), list(sample).selectNullsToFirst().toList()),
            () -> assertEquals(moveSelected(sample, Objects::isNull, false), list(sample).selectNullsToLast().toList()));
    }

    @Test
    @DisplayName("能够将映射结果为null的元素正确移动到流水线头部或尾部")
    void should_move_elements_which_has_null_mapped_result_to_the_head_or_tail_of_pipe_rightly() {
        List<Box<String>> sample = infiniteOddIntegerStrWithNullsPipe().map(Box::box).limit(NORMAL_SIZE).toList();
        assertAll(() -> assertEquals(moveSelected(sample, box -> box.get() == null, true),
                list(sample).selectNullsToFirstBy(Box::get).toList()),
            () -> assertEquals(moveSelected(sample, box -> box.get() == null, false),
                list(sample).selectNullsToLastBy(Box::get).toList()));
    }

    @Test
    @DisplayName("如果映射方法为映射自身，则根据映射结果为null移动元素到头部或尾部等同于移动null到头部或尾部")
    void should_move_elements_by_mapped_result_in_the_same_way_of_move_nulls_when_mapper_is_identify() {
        List<String> sample = generateOddIntegerStrWithNullsList();
        assertAll(
            () -> assertEquals(list(sample).selectNullsToFirstBy(identity()).toList(), list(sample).selectNullsToFirst().toList()),
            () -> assertEquals(list(sample).selectNullsToLastBy(identity()).toList(), list(sample).selectNullsToLast().toList()));
    }

    private <T> List<T> moveSelected(List<T> sample, Predicate<T> select, boolean selectedFirst) {
        Map<Boolean, List<T>> map = sample.stream().collect(partitioningBy(select));
        return addAll(map.get(selectedFirst), map.get(!selectedFirst));
    }

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
            () -> assertThrowsExactly(NullPointerException.class, () -> integerPipe().selectToFirst(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> integerPipe().selectToLast(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> integerPipe().selectNullsToFirstBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> integerPipe().selectNullsToLastBy(null)));
    }
}
