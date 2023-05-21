package com.oyealex.pipe.basis.api.policy;

/**
 * PartitionPolicy
 *
 * @author oyealex
 * @since 2023-05-21
 */
public enum PartitionPolicy {
    /** 标记数据为新分区的第一个数据 */
    BEGIN,
    /** 标记数据属于当前分区 */
    IN,
    /** 标记数据为当前分区的最后一个数据 */
    END,
}
