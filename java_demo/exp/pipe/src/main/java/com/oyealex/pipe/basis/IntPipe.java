package com.oyealex.pipe.basis;

import com.oyealex.pipe.BasePipe;
import com.oyealex.pipe.functional.LongIntPredicate;

import java.util.function.IntPredicate;

/**
 * IntPipe
 *
 * @author oyealex
 * @since 2023-03-03
 */
public interface IntPipe extends BasePipe<Integer, IntPipe> {
    IntPipe takeIf(IntPredicate predicate);

    IntPipe takeIfOrderly(LongIntPredicate predicate);
}
