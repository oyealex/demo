package com.oyealex.pipe.basis.api.policy;

import com.oyealex.pipe.basis.api.Pipe;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * MergeRemainingPolicy
 *
 * @author oyealex
 * @see Pipe#merge(Pipe, BiFunction, MergeRemainingPolicy)
 * @see Pipe#merge(Pipe, BiFunction, Function, Function, MergeRemainingPolicy)
 * @since 2023-05-17
 */
public enum MergeRemainingPolicy {
    /** 把缺少的流水元素当作{@code null}参与合并 */
    MERGE_AS_NULL,
    /** 如果当前流水线有多余，则保留当前流水线的元素 */
    KEEP_OURS,
    /** 如果其他流水线有多余，则保留当前流水线的元素 */
    KEEP_THEIRS,
    /** 保留任何多余的元素 */
    KEEP_REMAINING,
    /** 丢弃多余的元素 */
    DROP,
}