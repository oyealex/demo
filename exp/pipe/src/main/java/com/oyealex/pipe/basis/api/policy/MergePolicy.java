package com.oyealex.pipe.basis.api.policy;

import com.oyealex.pipe.basis.api.Pipe;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * MergePolicy
 *
 * @author oyealex
 * @see Pipe#merge(Pipe, BiFunction, MergeRemainingPolicy)
 * @see Pipe#merge(Pipe, BiFunction, Function, Function, MergeRemainingPolicy)
 * @since 2023-05-17
 */
public enum MergePolicy {
    /**
     * 选择当前流水线的数据，丢弃其他流水线的数据。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌───────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────╊━━━╉─────────┤ new │ ... 1 ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └───────────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    KEEP_OURS,
    /**
     * 选择其他流水线的数据，丢弃当前流水线的数据。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌───────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────╊━━━╉─────────┤ new │ ... A ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └───────────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    KEEP_THEIRS,
    /**
     * 选择当前流水线的数据，保留其他流水线的数据用于下次合并。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌───────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────┺━━━╉━━━┱─────┤ new │ ... 1 ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C   B ┃ A ┃ ... │     └───────────┘
     *        └─────────────┺━━━┹─────┘            └─────────────┺━━━┹─────┘
     * </code></pre>
     */
    PREFER_OURS,
    /**
     * 选择其他流水线的数据，保留当前流水线的数据用于下次合并。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────────┲━━━┱─────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3   2 ┃ 1 ┃ ... │     ┌───────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────┲━━━╉━━━┹─────┤ new │ ... A ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └───────────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    PREFER_THEIRS,
    /**
     * 选择两个流水线的数据，当前流水线的数据优先。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌─────────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────╊━━━╉─────────┤ new │ ... A 1 ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └─────────────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    OURS_FIRST,
    /**
     * 选择两个流水线的数据，其他流水线的数据优先。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌─────────────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────╊━━━╉─────────┤ new │ ... 1 A ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └─────────────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    THEIRS_FIRST,
    /**
     * 丢弃当前流水线的数据，保留其他流水线的数据用于下次合并。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌─────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────┺━━━╉━━━┱─────┤ new │ ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C   B ┃ A ┃ ... │     └─────┘
     *        └─────────────┺━━━┹─────┘            └─────────────┺━━━┹─────┘
     * </code></pre>
     */
    DROP_OURS,
    /**
     * 丢弃其他流水线的数据，保留当前流水线的数据用于下次合并。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────────┲━━━┱─────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3   2 ┃ 1 ┃ ... │     ┌─────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────┲━━━╉━━━┹─────┤ new │ ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └─────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    DROP_THEIRS,
    /**
     * 丢弃两个流水线的数据。
     * <pre><code>
     *        ┌─────────────┲━━━┱─────┐            ┌─────────┲━━━┱─────────┐
     * ours   │ ...   3   2 ┃ 1 ┃ ... │     ours   │ ...   3 ┃ 2 ┃ 1   ... │     ┌─────┐
     *        ├─────────────╊━━━╉─────┤ ==>        ├─────────╊━━━╉─────────┤ new │ ... │
     * theirs │ ...   C   B ┃ A ┃ ... │     theirs │ ...   C ┃ B ┃ A   ... │     └─────┘
     *        └─────────────┺━━━┹─────┘            └─────────┺━━━┹─────────┘
     * </code></pre>
     */
    DROP_BOTH,
}
