package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.oyealex.pipe.basis.Pipes.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对Pipe的其他测试。
 *
 * @author oyealex
 * @see Pipe#onClose(Runnable)
 * @see Pipe#close()
 * @since 2023-04-29
 */
class PipeOtherTest extends PipeTestBase {
    @Test
    @DisplayName("当流水线关闭时应当执行关闭方法")
    void should_execute_close_action_when_pipe_close() {
        List<String> callRecords = new ArrayList<>();
        of(ELEMENTS)
            .onClose(() -> callRecords.add("CallAfterInit"))
            .onClose(() -> callRecords.add("CallAfterInit#2"))
            .filter(Objects::nonNull)
            .onClose(() -> callRecords.add("callAfterFilter"))
            .close();
        assertEquals(List.of("CallAfterInit", "CallAfterInit#2", "callAfterFilter"), callRecords);
    }
}
