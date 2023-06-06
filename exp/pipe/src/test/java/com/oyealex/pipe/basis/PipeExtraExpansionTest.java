package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.oyealex.pipe.basis.Pipe.empty;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对流水线其他扩容系列API的测试
 *
 * @author oyealex
 * @see Pipe#disperse(Object)
 * @since 2023-06-05
 */
class PipeExtraExpansionTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确将给定数据分散到流水线中")
    void should_disperse_given_delimiter_into_pipe_rightly() {
        assertAll(() -> assertEquals(asList("0", SOME_STR, "1", SOME_STR, "2", SOME_STR, "3"),
                infiniteIntegerStrPipe().limit(4).disperse(SOME_STR).toList()),
            () -> assertEquals(singletonList("0"), infiniteIntegerStrPipe().limit(1).disperse(SOME_STR).toList()),
            () -> assertEquals(emptyList(), empty().disperse(SOME_STR).toList()));
    }

    @Test
    @DisplayName("能够正确将给定数据分散到流水线中，即使分隔数据是null")
    void should_disperse_null_delimiter_info_pipe_rightly() {
        assertEquals(asList("0", null, "1", null, "2", null, "3"),
            infiniteIntegerStrPipe().limit(4).disperse(null).toList());
    }

    // exception test
}
