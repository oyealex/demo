package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.function.Function;

/**
 * 针对流水线{@code distinct}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#distinct()
 * @see Pipe#distinctBy(Function)
 * @see Pipe#distinctByOrderly(LongBiFunction)
 * @since 2023-05-27
 */
class PipeDistinctTest extends PipeTestFixture {}
