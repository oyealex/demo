package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipe.constant;
import static com.oyealex.pipe.basis.Pipe.empty;
import static com.oyealex.pipe.basis.Pipe.list;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线判断系列API的测试。
 *
 * @author oyealex
 * @see Pipe#anyMatch(Predicate)
 * @see Pipe#allMatch(Predicate)
 * @see Pipe#noneMatch(Predicate)
 * @see Pipe#anyNull()
 * @see Pipe#allNull()
 * @see Pipe#noneNull()
 * @see Pipe#anyNullBy(Function)
 * @see Pipe#allNullBy(Function)
 * @see Pipe#noneNullBy(Function)
 * @since 2023-06-13
 */
class PipeJudgeTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确根据给定条件判断流水线元素")
    void should_judge_pipe_elements_by_given_predicate_rightly() {
        Predicate<Integer> predicate = PipeTestFixture::isOdd;
        List<Integer> nonMatch = infiniteIntegerPipe().takeIf(predicate.negate()).limit(NORMAL_SIZE).toList();
        List<Integer> someMatch = infiniteIntegerPipe().limit(NORMAL_SIZE).toList();
        List<Integer> allMatch = infiniteIntegerPipe().takeIf(predicate).limit(NORMAL_SIZE).toList();
        assertAll(() -> assertFalse(list(nonMatch).anyMatch(predicate)),
            () -> assertFalse(list(nonMatch).allMatch(predicate)),
            () -> assertTrue(list(nonMatch).noneMatch(predicate)),
            () -> assertTrue(list(someMatch).anyMatch(predicate)),
            () -> assertFalse(list(someMatch).allMatch(predicate)),
            () -> assertFalse(list(someMatch).noneMatch(predicate)),
            () -> assertTrue(list(allMatch).anyMatch(predicate)), () -> assertTrue(list(allMatch).allMatch(predicate)),
            () -> assertFalse(list(allMatch).noneMatch(predicate)));
    }

    @Test
    @DisplayName("能够正确判断流水线元素是否为null")
    void should_judge_null_pipe_elements_rightly() {
        List<String> nonNull = infiniteRandomStrPipe().dropNull().limit(NORMAL_SIZE).toList();
        List<Integer> someNull = infiniteOddIntegerWithNullsPipe().limit(NORMAL_SIZE).toList();
        List<Object> allNull = constant(null, NORMAL_SIZE).toList();
        assertAll(() -> assertFalse(list(nonNull).anyNull()), () -> assertFalse(list(nonNull).allNull()),
            () -> assertTrue(list(nonNull).noneNull()), () -> assertTrue(list(someNull).anyNull()),
            () -> assertFalse(list(someNull).allNull()), () -> assertFalse(list(someNull).noneNull()),
            () -> assertTrue(list(allNull).anyNull()), () -> assertTrue(list(allNull).allNull()),
            () -> assertFalse(list(allNull).noneNull()));
    }

    @Test
    @DisplayName("能够正确根据给定方法映射结果是否为null判断流水线元素")
    void should_judge_pipe_elements_by_given_mapper_rightly() {
        Function<Integer, String> mapper = value -> isOdd(value) ? null : "";
        List<Integer> nonNull = infiniteIntegerPipe().takeIf(value -> mapper.apply(value) != null)
            .limit(NORMAL_SIZE)
            .toList();
        List<Integer> someNull = infiniteIntegerPipe().limit(NORMAL_SIZE).toList();
        List<Integer> allNull = infiniteIntegerPipe().takeIf(value -> mapper.apply(value) == null)
            .limit(NORMAL_SIZE)
            .toList();
        assertAll(() -> assertFalse(list(nonNull).anyNullBy(mapper)),
            () -> assertFalse(list(nonNull).allNullBy(mapper)), () -> assertTrue(list(nonNull).noneNullBy(mapper)),
            () -> assertTrue(list(someNull).anyNullBy(mapper)), () -> assertFalse(list(someNull).allNullBy(mapper)),
            () -> assertFalse(list(someNull).noneNullBy(mapper)), () -> assertTrue(list(allNull).anyNullBy(mapper)),
            () -> assertTrue(list(allNull).allNullBy(mapper)), () -> assertFalse(list(allNull).noneNullBy(mapper)));
    }

    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().anyMatch(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().allMatch(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().noneMatch(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().anyNullBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().allNullBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().noneNullBy(null)));
    }
}
