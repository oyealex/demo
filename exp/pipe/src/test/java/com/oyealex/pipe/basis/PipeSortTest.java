package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.of;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线{@code sort}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#sort()
 * @see Pipe#sortReversely()
 * @see Pipe#sort(Comparator)
 * @see Pipe#sortBy(Function)
 * @see Pipe#sortBy(Function, Comparator)
 * @see Pipe#sortByOrderly(LongBiFunction)
 * @see Pipe#sortByOrderly(LongBiFunction, Comparator)
 * @since 2023-05-27
 */
class PipeSortTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确对元素按照自然顺序排序")
    void should_sort_elements_by_natural_order_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted().collect(toList()), list(sample).sort().toList());
    }

    @Test
    @DisplayName("能够正确对元素按照自然逆序排序")
    void should_sort_elements_reversely_by_natural_order_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(reverseOrder()).collect(toList()),
            list(sample).sortReversely().toList());
    }

    @Test
    @DisplayName("能够正确按照给定的比较器排序")
    void should_sort_elements_by_given_comparator_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(comparingInt(String::length)).collect(toList()),
            list(sample).sort(comparingInt(String::length)).toList());
    }

    @Test
    @DisplayName("当给定的比较器为null时按照自然顺序排序")
    void should_sort_elements_by_natural_order_when_given_comparator_is_null() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted().collect(toList()), list(sample).sort(null).toList());
    }

    @Test
    @DisplayName("能够正确按照映射结果排序")
    void should_sort_elements_by_mapped_result_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(comparingInt(String::length)).collect(toList()),
            list(sample).sortBy(String::length).toList());
    }

    @Test
    @DisplayName("如果映射方法为映射自身，则根据映射结果排序等同于根据自身排序")
    void should_sort_elements_by_mapped_result_in_the_same_way_of_sort_elements_self_when_mapper_is_identify() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertEquals(list(sample).sortBy(identity()).toList(), list(sample).sort().toList()),
            () -> assertEquals(list(sample).sortBy(identity(), reverseOrder()).toList(),
                list(sample).sort(reverseOrder()).toList()));
    }

    @Test
    @DisplayName("能够正确使用给定比较器按照映射后的结果对元素排序")
    void should_sort_elements_by_mapped_result_and_given_comparator_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(comparing(String::length, reverseOrder())).collect(toList()),
            list(sample).sortBy(String::length, reverseOrder()).toList());
    }

    @Test
    @DisplayName("当给定的比较器为null时，能够正确按照映射后的结果以自然顺序对元素排序")
    void should_sort_elements_by_mapped_result_in_natural_order_when_given_comparator_is_null() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().sorted(comparing(String::length, naturalOrder())).collect(toList()),
            list(sample).sortBy(String::length, null).toList());
    }

    @Test
    @DisplayName("能够正确按照支持次序的映射结果排序")
    void should_sort_elements_by_mapped_result_orderly_rightly() {
        List<String> sample = infiniteRandomStrPipe().distinct().limit(NORMAL_SIZE).toList();
        IntBox counter = IntBox.box();
        Map<String, Integer> orderMap = new HashMap<>();
        assertEquals(sample.stream()
            .peek(value -> orderMap.put(value, counter.getAndIncrement()))
            .sorted(comparingInt(orderMap::get))
            .collect(toList()), list(sample).sortByOrderly((order, value) -> order).toList());
    }

    @Test
    @DisplayName("能够正确使用给定比较器按照有次序地映射后的结果对元素排序")
    void should_sort_elements_by_orderly_mapped_result_and_given_comparator_rightly() {
        List<String> sample = infiniteRandomStrPipe().distinct().limit(NORMAL_SIZE).toList();
        IntBox counter = IntBox.box();
        Map<String, Integer> orderMap = new HashMap<>();
        assertEquals(sample.stream()
            .peek(value -> orderMap.put(value, counter.getAndIncrement()))
            .sorted(comparing(orderMap::get, reverseOrder()))
            .collect(toList()), list(sample).sortByOrderly((order, value) -> order, reverseOrder()).toList());
    }

    @Test
    @DisplayName("当给定的比较器为null时，能够正确按照有次序地映射后的结果以自然顺序对元素排序")
    void should_sort_elements_by_orderly_mapped_result_when_given_comparator_is_null() {
        List<String> sample = infiniteRandomStrPipe().distinct().limit(NORMAL_SIZE).toList();
        IntBox counter = IntBox.box();
        Map<String, Integer> orderMap = new HashMap<>();
        assertEquals(sample.stream()
            .peek(value -> orderMap.put(value, counter.getAndIncrement()))
            .sorted(comparing(orderMap::get, naturalOrder()))
            .collect(toList()), list(sample).sortByOrderly((order, value) -> order, null).toList());
    }

    // optimization test

    @Test
    @DisplayName("对已经自然有序或自然逆序的流水线进行任意自然排序或自然逆序不会实际执行排序")
    void should_not_do_sort_actually_when_the_pipe_is_already_ordered() {
        assertAll(() -> testForSortOptimization(Pipe::sort, Pipe::sort),
            () -> testForSortOptimization(Pipe::sort, Pipe::sortReversely),
            () -> testForSortOptimization(Pipe::sortReversely, Pipe::sort),
            () -> testForSortOptimization(Pipe::sortReversely, Pipe::sortReversely));
    }

    private void testForSortOptimization(Function<Pipe<ComparableTestDouble>, Pipe<ComparableTestDouble>> prepare,
        Function<Pipe<ComparableTestDouble>, Pipe<ComparableTestDouble>> sort) {
        assertTrue(infiniteRandomStrPipe().limit(NORMAL_SIZE).map(ComparableTestDouble::new).chain(prepare) // 先准备
            .peek(ComparableTestDouble::reset) // 重置记录
            .chain(sort) // 再排序
            .toList().stream().noneMatch(ComparableTestDouble::isCompareToCalled));
    }

    // exception test

    @Test
    @DisplayName("如果元素没有实现Comparable接口，则在排序时抛出异常")
    void should_throw_exception_if_sort_elements_which_do_not_implement_comparable() {
        assertAll(() -> assertThrowsExactly(ClassCastException.class,
                () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sort().run()),
            () -> assertThrowsExactly(ClassCastException.class,
                () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sortReversely().run()),
            () -> assertThrowsExactly(ClassCastException.class,
                () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sort(null).run()));
    }

    @Test
    @DisplayName("如果给定的映射方法为null，则会抛出异常")
    void should_throw_exception_when_the_given_mapper_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().sortBy(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().sortBy(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().sortByOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().sortByOrderly(null, naturalOrder())));
    }

    private static class UnComparableTestDouble {}
}
