package com.oyealex.pipe.policy;

import com.oyealex.pipe.basis.Pipe;

import java.util.function.BiFunction;

/**
 * MergeRemainingPolicy
 *
 * @author oyealex
 * @see Pipe#merge(Pipe, BiFunction, MergeRemainingPolicy)
 * @see Pipe#merge(Pipe, BiFunction, BiFunction, BiFunction, MergeRemainingPolicy)
 * @since 2023-05-17
 */
public enum MergeRemainingPolicy {
    /** 把缺少的流水元素当作{@code null}参与合并 */
    MERGE_AS_NULL,
    /** 如果当前流水线有多余，则保留当前流水线的元素 */
    TAKE_OURS,
    /** 如果其他流水线有多余，则保留其他流水线的元素 */
    TAKE_THEIRS,
    /** 保留任何多余的元素 */
    TAKE_REMAINING,
    /** 丢弃多余的元素 */
    DROP,
}
