package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线{@code distinct}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#distinct()
 * @see Pipe#distinctBy(Function)
 * @see Pipe#distinctByOrderly(LongBiFunction)
 * @since 2023-05-27
 */
class PipeDistinctTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确对元素去重")
    void should_distinct_elements_rightly() {
        List<String> duplicated = duplicateList(generateIntegerStrList());
        assertEquals(duplicated.stream().distinct().collect(toList()), list(duplicated).distinct().toList());
    }

    @Test
    @DisplayName("能够正确根据映射结果对元素去重")
    void should_distinct_by_mapper_rightly() {
        List<String> duplicated = duplicateList(generateRandomStrList());
        Set<Integer> seen = new HashSet<>();
        assertEquals(duplicated.stream().filter(val -> seen.add(val.length())).collect(toList()),
            list(duplicated).distinctBy(String::length).toList());
    }

    @Test
    @DisplayName("即使映射方法返回了null也能正常去重")
    void should_distinct_rightly_even_if_mapper_get_null_result() {
        List<String> duplicated = duplicateList(generateRandomStrList());
        Set<Integer> seen = new HashSet<>();
        assertEquals(duplicated.stream().filter(val -> seen.add(null)).collect(toList()),
            list(duplicated).distinctBy(ignored -> null).toList());
    }

    @Test
    @DisplayName("能够正确依次根据映射结果对元素去重")
    void should_distinct_by_mapper_orderly_rightly() {
        List<String> duplicated = duplicateList(generateRandomStrList());
        IntBox counter = IntBox.box();
        Set<Integer> seen = new HashSet<>();
        assertEquals(duplicated.stream().filter(ignored -> seen.add(counter.getAndIncrement() % 5)).collect(toList()),
            list(duplicated).distinctByOrderly((order, val) -> order % 5).toList());
    }

    @Test
    @DisplayName("即使按次序映射方法返回了null也能正常去重")
    void should_distinct_orderly_rightly_even_if_mapper_get_null_result() {
        List<String> duplicated = duplicateList(generateRandomStrList());
        Set<Integer> seen = new HashSet<>();
        assertEquals(duplicated.stream().filter(ignored -> seen.add(null)).collect(toList()),
            list(duplicated).distinctByOrderly((order, val) -> null).toList());
    }

    // optimization test

    @Test
    @DisplayName("如果流水线已经去重，则不会真正执行去重")
    void should_not_do_distinct_actually_when_the_pipe_is_already_distinct() {
        assertTrue(infiniteRandomStrPipe().limit(NORMAL_SIZE)
            .map(DistinctTestDouble::new)
            .distinct()
            .peek(DistinctTestDouble::reset) // 重置记录
            .distinct()
            .toList()
            .stream()
            .noneMatch(DistinctTestDouble::isEqualsOrHashCodeCalled));
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().distinctBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().distinctByOrderly(null)));
    }

    private static class DistinctTestDouble {
        private final String value;

        private boolean equalsOrHashCodeCalled = false;

        private DistinctTestDouble(String value) {
            this.value = value;
        }

        private boolean isEqualsOrHashCodeCalled() {
            return equalsOrHashCodeCalled;
        }

        private void reset() {
            equalsOrHashCodeCalled = false;
        }

        @Override
        public boolean equals(Object o) {
            equalsOrHashCodeCalled = true;
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DistinctTestDouble that = (DistinctTestDouble) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            equalsOrHashCodeCalled = true;
            return Objects.hash(value);
        }
    }
}
