package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对Pipe的过滤类方法的测试。
 *
 * @author oyealex
 * @see Pipe#keepIf(Predicate)
 * @see Pipe#dropIf(Predicate)
 * @see Pipe#filterEnumerated(LongBiPredicate)
 * @since 2023-04-28
 */
class PipeFilterTest extends PipeTestBase {
    @Test
    @DisplayName("应当正确过滤元素")
    void should_filter_elements_rightly() {
        List<String> res = of(ELEMENTS).keepIf(str -> str.length() > 3).toList();
        assertEquals(stream(ELEMENTS).filter(str -> str.length() > 3).collect(toList()), res);
    }

    @Test
    @DisplayName("应当正确反向过滤元素")
    void should_reversely_filter_elements_rightly() {
        List<String> res = of(ELEMENTS).dropIf(str -> str.length() > 3).toList();
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
    @DisplayName("在支持访问元素次序的过滤接口中能够正常访问元素次序")
    void should_be_able_to_visit_enumeration_number_rightly() {
        TreeMap<Long, String> visited = new TreeMap<>();
        TreeMap<Long, String> expected = new TreeMap<>();
        of(ELEMENTS).filterEnumerated((index, value) -> visited.put(index, value) != null).forEach(v -> {});
        int[] index = new int[1];
        stream(ELEMENTS).forEach(value -> expected.put((long) index[0]++, value));
        assertEquals(expected, visited);
    }

    @Test
    @DisplayName("能够正确保留元素直到给定条件首次为假")
    void should_keep_elements_while_predicate_is_matched() {
        List<String> res = of(ELEMENTS).keepWhile(value -> value.endsWith(",")).toList();
        assertEquals(stream(ELEMENTS).takeWhile(value -> value.endsWith(",")).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确丢弃元素直到给定条件首次为假")
    void should_drop_elements_while_predicate_is_matched() {
        List<String> res = of(ELEMENTS).dropWhile(value -> value.endsWith(",")).toList();
        assertEquals(stream(ELEMENTS).dropWhile(value -> value.endsWith(",")).collect(toList()), res);
    }

    @Test
    @DisplayName("当给定的断言为空时应当抛出异常")
    void should_throw_exception_when_given_predicate_is_null() {
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().keepIf(null));
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().dropIf(null));
        assertThrowsExactly(NullPointerException.class, () -> Pipes.empty().filterEnumerated(null));
    }
}
