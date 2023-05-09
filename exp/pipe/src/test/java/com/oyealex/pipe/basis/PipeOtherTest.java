package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import com.oyealex.pipe.basis.api.Pipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的其他测试。
 *
 * @author oyealex
 * @see Pipe#skip(long)
 * @see Pipe#limit(long)
 * @see Pipe#distinct()
 * @see Pipe#distinctBy(Function)
 * @see Pipe#onClose(Runnable)
 * @see Pipe#close()
 * @since 2023-04-29
 */
class PipeOtherTest extends PipeTestBase {
    @Test
    @DisplayName("当流水线关闭时应当执行关闭方法")
    void should_execute_close_action_when_pipe_close() {
        List<String> callRecords = new ArrayList<>();
        of(ELEMENTS).onClose(() -> callRecords.add("CallAfterInit"))
            .onClose(() -> callRecords.add("CallAfterInit#2"))
            .keepIf(Objects::nonNull)
            .onClose(() -> callRecords.add("callAfterFilter"))
            .close();
        assertEquals(List.of("CallAfterInit", "CallAfterInit#2", "callAfterFilter"), callRecords);
    }

    @Test
    @DisplayName("能够正确限制元素数量")
    void should_limit_elements_rightly() {
        List<String> res = of(ELEMENTS).limit(3).toList();
        assertEquals(stream(ELEMENTS).limit(3).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确跳过元素数量")
    void should_skip_elements_rightly() {
        List<String> res = of(ELEMENTS).skip(3).toList();
        assertEquals(stream(ELEMENTS).skip(3).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确对元素切片")
    void should_slice_elements_rightly() {
        List<String> res = of(ELEMENTS).slice(2, 5).toList();
        assertEquals(stream(ELEMENTS).skip(2).limit(3).collect(toList()), res);
    }

    @Test
    @DisplayName("能够正确对元素去重")
    void should_distinct_elements_rightly() {
        List<String> res = of(ELEMENTS).distinct().toList();
        assertEquals(stream(ELEMENTS).distinct().collect(toList()), res);
    }

    @Test
    @DisplayName("能够根据映射的Key对元素去重")
    void should_distinct_elements_by_key_rightly() {
        List<String> res = of(ELEMENTS).distinctBy(String::length).toList();
        Set<Integer> seen = new HashSet<>();
        assertEquals(stream(ELEMENTS).filter(value -> seen.add(value.length())).collect(toList()), res);
    }
}
