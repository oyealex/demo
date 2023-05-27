package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipes.list;
import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
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
        assertEquals(sample.stream().sorted(reverseOrder()).collect(toList()), list(sample).sortReversely().toList());
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
    @DisplayName("对已经自然有序的流水线执行自然排序不会实际执行排序")
    void should_do_not_sort_elements_actually_when_the_pipe_is_already_natural_ordered() {
        List<ComparableTestDouble> result = infiniteRandomStrPipe().limit(NORMAL_SIZE)
            .map(ComparableTestDouble::new)
            .sort() // 先排序
            .peek(ComparableTestDouble::reset) // 重置记录
            .sort() // 再次排序，预期不会执行实际的排序
            .toList();
        assertTrue(result.stream().noneMatch(ComparableTestDouble::isCompareToCalled));
    }

    @Test
    @DisplayName("对已经自然逆序的流水线执行自然排序不会实际执行排序")
    void should_do_not_sort_elements_actually_when_the_pipe_is_already_natural_ordered_reversely() {
        List<ComparableTestDouble> result = infiniteRandomStrPipe().limit(NORMAL_SIZE)
            .map(ComparableTestDouble::new)
            .sortReversely() // 先逆序排序
            .peek(ComparableTestDouble::reset) // 重置记录
            .sort() // 再次排序，预期不会执行实际的排序
            .toList();
        assertTrue(result.stream().noneMatch(ComparableTestDouble::isCompareToCalled));
    }

    // exception test

    @Test
    @DisplayName("如果元素没有实现Comparable接口，则在排序时抛出异常")
    void should_throw_exception_if_sort_elements_which_do_not_implement_comparable() {
        assertThrowsExactly(ClassCastException.class,
            () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sort().run());
        assertThrowsExactly(ClassCastException.class,
            () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sortReversely().run());
        assertThrowsExactly(ClassCastException.class,
            () -> of(new UnComparableTestDouble(), new UnComparableTestDouble()).sort(null).run());
    }

    private static class UnComparableTestDouble {}

    private static class ComparableTestDouble implements Comparable<ComparableTestDouble> {
        private final String str;

        private boolean compareToCalled = false;

        private ComparableTestDouble(String str) {
            this.str = str;
        }

        private boolean isCompareToCalled() {
            return compareToCalled;
        }

        @Override
        public int compareTo(@NotNull PipeSortTest.ComparableTestDouble o) {
            compareToCalled = true;
            return str.length() - o.str.length();
        }

        private void reset() {
            compareToCalled = false;
        }
    }
}
