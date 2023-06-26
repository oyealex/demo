package com.oyealex.pipe.policy;

/**
 * 分区策略
 * <p/>
 * 用于指导分区方法如何对元素进行分区。
 *
 * @author oyealex
 * @since 2023-05-21
 */
public enum PartitionPolicy {
    /**
     * 结束当前分区（如果存在），开启新分区，当前元素为新分区的第一个数据。
     * <p/>
     * 一个示意：
     * <pre><code>
     * current element: C
     *
     *  pre partition │ new partition
     *   ───────────┐ │ ┌───────
     *    ... 4 5 6 │ │ │ C ...
     *   ───────────┘ │ └───────
     * </code></pre>
     */
    BEGIN,
    /**
     * 追加当前元素到当前分区中（如果没有分区则开启分区）。
     * <p/>
     * 一个示意：
     * <pre><code>
     * current element: C
     *
     *  current partition
     *   ─────────────
     *    ... 4 5 6 C
     *   ─────────────
     * </code></pre>
     */
    IN,
    /**
     * 追加当前元素到当前分区中（如果没有分区则开启分区），然后结束当前分区。
     * <p/>
     * 一个示意：
     * <pre><code>
     * current element: C
     *
     *  current partition
     *   ─────────────┐
     *    ... 4 5 6 C │
     *   ─────────────┘
     * </code></pre>
     */
    END,
}
