package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.functional.IntBiPredicate;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对Pipe的过滤类方法的测试。
 *
 * @author oyealex
 * @see Pipe#filter(Predicate)
 * @see Pipe#filterReversely(Predicate)
 * @see Pipe#filterEnumerated(IntBiPredicate)
 * @see Pipe#filterEnumeratedLong(LongBiPredicate)
 * @since 2023-04-28
 */
class PipeFilterTest extends PipeTestBase {
    @Test
    @DisplayName("应当正确过滤元素")
    void should_filter_elements_rightly() {
        List<String> res = of(ELEMENTS).filter(str -> str.length() > 3).toList();
        assertEquals(stream(ELEMENTS).filter(str -> str.length() > 3).collect(toList()), res);
    }

    @Test
    @DisplayName("应当正确反向过滤元素")
    void should_reversely_filter_elements_rightly() {
        List<String> res = of(ELEMENTS).filterReversely(str -> str.length() > 3).toList();
        assertEquals(stream(ELEMENTS).filter(str -> str.length() <= 3).collect(toList()), res);
    }

    @Test
    @DisplayName("应当正确根据次序编号过滤元素")
    void should_filter_elements_by_enumeration_number_rightly() {
        List<String> res = of(ELEMENTS).filterEnumerated((index, value) -> index <= 3 || value.length() > 5).toList();
        int[] index = new int[1];
        assertEquals(stream(ELEMENTS).filter(value -> index[0]++ <= 3 || value.length() > 5).collect(toList()), res);
    }

    @Test
    @Disabled("OOM")
    @DisplayName("应当正确根据次序编号过滤超过整形最大值数量的元素")
    void should_filter_elements_by_enumeration_number_on_elements_more_than_the_max_value_of_integer_rightly() {
        List<Long> res = Pipes.iterate(1L, i -> i + 1).limit(MAX_VALUE + 1L)
            .filterEnumeratedLong((index, value) -> index > MAX_VALUE).toList();
        long[] index = new long[1];
        List<Long> expected = Stream.iterate(1L, i -> i + 1).limit(MAX_VALUE + 1L)
            .filter(value -> index[0]++ > MAX_VALUE).collect(toList());
        assertEquals(expected, res);
    }

    @Test
    @DisplayName("在支持访问元素次序的过滤接口中能够正常访问元素次序")
    void should_be_able_to_visit_enumeration_number_rightly() {
        TreeMap<Long, String> visited = new TreeMap<>();
        TreeMap<Long, String> expected = new TreeMap<>();
        of(ELEMENTS).filterEnumeratedLong((index, value) -> visited.put(index, value) != null).forEach(v -> {});
        int[] index = new int[1];
        stream(ELEMENTS).forEach(value -> expected.put((long) index[0]++, value));
        assertEquals(expected, visited);
    }

    @Test
    @DisplayName("当给定的断言为空时应当抛出异常")
    void should_throw_exception_when_given_predicate_is_null() {
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().filter(null));
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().filterReversely(null));
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().filterEnumerated(null));
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().filterEnumeratedLong(null));
    }
}
