package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 针对流水线{@code sort}系列API的测试用例。
 *
 * @author oyealex
 * @see Pipe#sort()
 * @see Pipe#sort(Comparator)
 * @see Pipe#sortBy(Function)
 * @see Pipe#sortBy(Function, Comparator)
 * @see Pipe#sortByOrderly(LongBiFunction)
 * @see Pipe#sortByOrderly(LongBiFunction, Comparator)
 * @since 2023-05-27
 */
class PipeSortTest extends PipeTestFixture {}
