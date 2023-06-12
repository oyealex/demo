package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static com.oyealex.pipe.basis.Pipe.list;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对流水线其他收集元素到容器系列API的测试
 *
 * @author oyealex
 * @see Pipe#join()
 * @see Pipe#join(CharSequence)
 * @see Pipe#join(CharSequence, CharSequence, CharSequence)
 * @since 2023-06-05
 */
class PipeExtraCollectTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确将流水线元素收集为字符串")
    void should_collect_pipe_elements_to_string_rightly() {
        List<String> sample = generateRandomStrList();
        assertAll(() -> assertEquals(join("", sample), list(sample).join()),
            () -> assertEquals(join(SOME_STR, sample), list(sample).join(SOME_STR)),
            () -> assertEquals(sample.stream().collect(joining(SOME_STR, "[", "]")),
                list(sample).join(SOME_STR, "[", "]")));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集为字符串，即使流水线中包含null元素")
    void should_collect_pipe_elements_to_string_rightly_when_the_pipe_contains_null() {
        List<String> sample = generateOddIntegerStrWithNullsList();
        assertAll(() -> assertEquals(join("", sample), list(sample).join()),
            () -> assertEquals(join(SOME_STR, sample), list(sample).join(SOME_STR)),
            () -> assertEquals(sample.stream().collect(joining(SOME_STR, "[", "]")),
                list(sample).join(SOME_STR, "[", "]")));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集为字符串，对于非字符串元素流水线会自动转为字符串")
    void should_convert_non_string_elements_to_string_and_collect_to_list_rightly() {
        List<Integer> sample = generateIntegerList();
        assertAll(() -> assertEquals(sample.stream().map(Objects::toString).collect(joining()), list(sample).join()),
            () -> assertEquals(sample.stream().map(Objects::toString).collect(joining(SOME_STR)),
                list(sample).join(SOME_STR)),
            () -> assertEquals(sample.stream().map(Objects::toString).collect(joining(SOME_STR, "[", "]")),
                list(sample).join(SOME_STR, "[", "]")));
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        // TODO 2023-06-10 22:26 按需补充
    }
}
