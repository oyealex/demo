package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.oyealex.pipe.basis.Pipes.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的短路功能测试。
 *
 * @author oyealex
 * @see Pipe#limit(long)
 * @see Pipe#slice(long, long)
 * @see Pipe#findFirst()
 * @see Pipe#findLast()
 * @see Pipe#findAny()
 * @since 2023-04-29
 */
class PipeShortCircuitTest extends PipeTestBase {
    @Test
    @DisplayName("对限制数量之后的元素进行短路操作，不会处理任何多余的元素")
    void should_not_process_any_other_elements_after_limit() {
        List<String> collectedBeforeLimit = new ArrayList<>();
        List<String> res = of(ELEMENTS).peek(collectedBeforeLimit::add).limit(3).toList();
        List<String> expected = stream(ELEMENTS).limit(3).collect(toList());
        assertEquals(expected, res);
        assertEquals(expected, collectedBeforeLimit);
    }

    @Test
    @DisplayName("对切片右边界之后的元素进行短路操作，不会处理任何多余的元素")
    void should_not_process_any_other_elements_after_the_right_border_of_slice() {
        List<String> collectedBeforeSlice = new ArrayList<>();
        List<String> res = of(ELEMENTS).peek(collectedBeforeSlice::add).slice(3, 5).toList();
        assertEquals(stream(ELEMENTS).skip(3).limit(2).collect(toList()), res);
        assertEquals(stream(ELEMENTS).limit(5).collect(toList()), collectedBeforeSlice);
    }
}
