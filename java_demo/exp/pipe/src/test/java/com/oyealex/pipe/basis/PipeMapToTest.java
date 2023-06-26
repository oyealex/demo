package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.Disabled;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * 针对流水线{@code mapTo}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#mapToInt(ToIntFunction)
 * @see Pipe#mapToIntOrderly(ToIntFunction)
 * @see Pipe#mapToLong(ToLongFunction)
 * @see Pipe#mapToLongOrderly(ToLongFunction)
 * @see Pipe#mapToDouble(ToDoubleFunction)
 * @see Pipe#mapToDoubleOrderly(ToDoubleFunction)
 * @since 2023-05-26
 */
@Disabled("mapTo系列API尚未实现")
class PipeMapToTest extends PipeTestFixture {}
