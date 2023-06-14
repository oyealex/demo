package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipe.empty;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线构造系列API的测试。
 *
 * @author oyealex
 * @see Pipe#groupValues(Function)
 * @see Pipe#groupFlatValues(Function)
 * @see Pipe#group(Function)
 * @since 2023-06-15
 */
class PipeGroupTest extends PipeTestFixture {
    // TODO 2023-06-15 01:16 continue
    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().peek(null)));
    }
}
