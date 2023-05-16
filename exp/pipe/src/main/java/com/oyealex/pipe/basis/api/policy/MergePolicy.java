package com.oyealex.pipe.basis.api.policy;

/**
 * MergePolicy
 *
 * @author oyealex
 * @since 2023-05-17
 */
public enum MergePolicy {
    /** 选择当前流水线的数据，丢弃其他流水线的数据 */
    SELECT_OURS,
    /** 选择其他流水线的数据，丢弃当前流水线的数据 */
    SELECT_THEIRS,
    /** 选择当前流水线的数据，保留其他流水线的数据用于下次合并 */
    PREFER_OURS,
    /** 选择其他流水线的数据，保留当前流水线的数据用于下次合并 */
    PREFER_THEIRS,
    /** 选择两个流水线的数据，当前流水线的数据优先 */
    OURS_FIRST,
    /** 选择两个流水线的数据，其他流水线的数据优先 */
    THEIRS_FIRST,
    /** 丢弃当前流水线的数据，保留其他流水线的数据用于下次合并 */
    DROP_OURS,
    /** 丢弃其他流水线的数据，保留当前流水线的数据用于下次合并 */
    DROP_THEIRS,
    /** 丢弃两个流水线的数据 */
    DROP_BOTH,
}
