package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {}
}
