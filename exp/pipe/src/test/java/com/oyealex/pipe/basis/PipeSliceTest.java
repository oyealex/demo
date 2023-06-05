package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对流水线切片API的测试。
 *
 * @author oyealex
 * @see Pipe#skip(long)
 * @see Pipe#skip(long, Predicate)
 * @see Pipe#limit(long)
 * @see Pipe#limit(long, Predicate)
 * @see Pipe#slice(long, long)
 * @see Pipe#slice(long, long, Predicate)
 * @since 2023-06-03
 */
class PipeSliceTest extends PipeTestFixture {
    private static final int SKIP = 5;

    private static final int LIMIT = 4;

    @Test
    @DisplayName("能够正确跳过和限制元素")
    void should_skip_and_limit_elements_rightly() {
        List<String> sample = generateRandomStrList();
        assertAll(
            () -> assertEquals(sample.stream().skip(SKIP).collect(toList()), list(sample).skip(SKIP).toList()),
            () -> assertEquals(sample.stream().limit(LIMIT).collect(toList()),
                list(sample).limit(LIMIT).toList()));
    }

    @Test
    @DisplayName("能够正确的根据断言跳过和限制元素")
    void should_skip_and_limit_elements_by_predicate_rightly() {
        List<Integer> sample = generateIntegerList();
        IntBox skipCounter = IntBox.box();
        IntBox limitCounter = IntBox.box();
        assertAll(
            () -> assertEquals(sample.stream().filter(value -> predicatedSkip(skipCounter, value)).collect(toList()),
                list(sample).skip(SKIP, PipeTestFixture::isOdd).toList()),
            () -> assertEquals(sample.stream().filter(value -> predicatedLimit(limitCounter, value)).collect(toList()),
                list(sample).limit(LIMIT, PipeTestFixture::isOdd).toList()));
    }

    @Test
    @DisplayName("能够正确对流水线元素切片")
    void should_slice_elements_rightly() {
        List<String> sample = generateRandomStrList();
        assertEquals(sample.stream().skip(SKIP).limit(LIMIT).collect(toList()),
            list(sample).slice(SKIP, SKIP + LIMIT).toList());
    }

    @Test
    @DisplayName("能够正确对流水线元素根据断言切片")
    void should_slice_elements_by_predicate_rightly() {
        List<Integer> sample = generateIntegerList();
        IntBox skipCounter = IntBox.box();
        IntBox limitCounter = IntBox.box();
        assertEquals(sample.stream()
            .filter(value -> predicatedSkip(skipCounter, value))
            .filter(value -> predicatedLimit(limitCounter, value))
            .collect(toList()), list(sample).slice(SKIP, SKIP + LIMIT, PipeTestFixture::isOdd).toList());
    }

    private static boolean predicatedSkip(IntBox skipCounter, Integer value) {
        if (skipCounter.get() >= SKIP) {
            return true;
        } else {
            if (isOdd(value)) {
                skipCounter.incrementAndGet();
            }
            return false;
        }
    }

    private static boolean predicatedLimit(IntBox limitCounter, Integer value) {
        if (limitCounter.get() < LIMIT) {
            if (isOdd(value)) {
                limitCounter.incrementAndGet();
            }
            return true;
        }
        return false;
    }

    // optimized test

    @Test
    @DisplayName("如果跳过元素数量为0或保留元素数量为Long最大值，则得到原流水线")
    void should_get_source_pipe_when_skip_size_is_0_or_limit_size_is_max_value_of_long() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertEquals(sample, list(sample).skip(0).toList()),
            () -> assertEquals(sample, list(sample).skip(0, String::isEmpty).toList()),
            () -> assertEquals(sample, list(sample).limit(Long.MAX_VALUE).toList()),
            () -> assertEquals(sample, list(sample).limit(Long.MAX_VALUE, String::isEmpty).toList()));
    }

    @Test
    @DisplayName("如果跳过元素数量为Long最大值或保留元素数量为0，则得到空流水线")
    void should_get_empty_pipe_when_skip_size_is_max_value_of_long_or_limit_size_is_0() {
        assertAll(() -> assertTrue(infiniteRandomStrPipe().skip(Long.MAX_VALUE).toList().isEmpty()),
            () -> assertTrue(infiniteRandomStrPipe().skip(Long.MAX_VALUE, String::isEmpty).toList().isEmpty()),
            () -> assertTrue(infiniteRandomStrPipe().limit(0).toList().isEmpty()),
            () -> assertTrue(infiniteRandomStrPipe().limit(0, String::isEmpty).toList().isEmpty()));
    }

    @Test
    @DisplayName("如果切片范围宽度为0，则得到空流水线")
    void should_get_empty_pipe_when_slice_with_zero_range_width() {
        assertAll(() -> assertTrue(infiniteRandomStrPipe().slice(SKIP, SKIP).toList().isEmpty()),
            () -> assertTrue(infiniteRandomStrPipe().slice(SKIP, SKIP, String::isEmpty).toList().isEmpty()));
    }

    @Test
    @DisplayName("如果切片范围款对为最大，则得到原流水线")
    void should_get_source_pipe_when_slice_with_max_range_width() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertEquals(sample, list(sample).slice(0, Long.MAX_VALUE).toList()),
            () -> assertEquals(sample, list(sample).slice(0, Long.MAX_VALUE, String::isEmpty).toList()));
    }

    // exception test

    @Test
    @DisplayName("当数量或索引参数无效时抛出异常")
    void should_throw_exception_when_size_or_index_is_invalid() {
        assertAll(() -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerPipe().skip(-1)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().skip(-1, PipeTestFixture::isOdd)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerPipe().limit(-1)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().limit(-1, PipeTestFixture::isOdd)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerPipe().slice(-1, LIMIT)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerPipe().slice(SKIP, -1)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteIntegerPipe().slice(-1, -1)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().slice(SKIP, SKIP - 1)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().slice(-1, SKIP + LIMIT, PipeTestFixture::isOdd)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().slice(SKIP, -1, PipeTestFixture::isOdd)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().slice(-1, -1, PipeTestFixture::isOdd)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteIntegerPipe().slice(SKIP, SKIP - 1, PipeTestFixture::isOdd)));
    }

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().skip(SKIP, null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().limit(SKIP, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().slice(SKIP, SKIP + LIMIT, null)));
    }
}
