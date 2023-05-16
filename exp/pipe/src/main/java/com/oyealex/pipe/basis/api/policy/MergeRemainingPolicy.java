package com.oyealex.pipe.basis.api.policy;

/**
 * MergeRemainingPolicy
 *
 * @author oyealex
 * @since 2023-05-17
 */
public enum MergeRemainingPolicy {
    /** 把缺少的流水元素当作{@code null}参与合并 */
    MERGE_AS_NULL,
    /** 如果当前流水线有多余，则保留当前流水线的元素 */
    SELECT_OURS,
    /** 如果其他流水线有多余，则保留当前流水线的元素 */
    SELECT_THEIRS,
    /** 保留任何多余的元素 */
    SELECT_REMAINING,
    /** 丢弃多余的元素 */
    DROP,
}
