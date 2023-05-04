package com.oyealex.pipe.flag;

import java.util.Spliterator;

/**
 * 流水线标记，用于辅助对流水线的优化。
 *
 * @author oyealex
 * @since 2023-05-05
 */
public enum PipeFlag {
    /** 所有数据基于{@link Object#equals(Object)}比较规则判定为唯一 */
    NATURAL_DISTINCT(0),

    /** 所有数据均实现了{@link Comparable}并按照自然顺序排序 */
    NATURAL_SORTED(1),

    /** 所有数据均实现了{@link Comparable}并按照自然顺序逆序排序 */
    NATURAL_REVERSED_SORTED(2),

    /** 数据源是有限的 */
    SIZED(3),

    /** 所有数据都不为{@code null} */
    NONNULL(4),

    /** 流水线操作可以被短路 */
    SHORT_CIRCUIT(5),
    ;

    public static final int IS_NATURAL_DISTINCT = NATURAL_DISTINCT.bit;

    public static final int IS_NATURAL_SORTED = NATURAL_SORTED.bit;

    public static final int IS_NATURAL_REVERSED_SORTED = NATURAL_REVERSED_SORTED.bit;

    public static final int IS_SIZED = SIZED.bit;

    public static final int IS_NONNULL = NONNULL.bit;

    public static final int IS_SHORT_CIRCUIT = SHORT_CIRCUIT.bit;

    private static final int RELATED_SPLITERATOR_FLAG_MAS = Spliterator.DISTINCT | Spliterator.SORTED |
        Spliterator.SIZED | Spliterator.NONNULL;

    private final int bit;

    PipeFlag(int offset) {
        this.bit = 1 << offset;
    }

    public static int combine(int flag, int opFlag) {
        // TODO 2023-05-05 02:05 需要转换为标准方式
        return 0;
    }

    /**
     * 判断是否设置此标记
     *
     * @param flag 标记值
     * @return 是否设置此标记
     */
    public boolean isSet(int flag) {
        return (flag & bit) == 1;
    }

    /**
     * 判断是否取消设置此标记
     *
     * @param flag 标记值
     * @return 是否取消设置此标记
     */
    public boolean isUnset(int flag) {
        return (flag & bit) == 0;
    }

    /**
     * 从给定的拆分器中构建流水线标记
     *
     * @param spliterator 拆分器
     * @return 流水线标记
     */
    public static int fromSpliterator(Spliterator<?> spliterator) {
        int characteristics = spliterator.characteristics();
        int flag = 0;
        if ((characteristics & Spliterator.DISTINCT) == Spliterator.DISTINCT) {
            flag |= IS_NATURAL_DISTINCT;
        }
        if ((characteristics & Spliterator.SORTED) == Spliterator.SORTED && spliterator.getComparator() == null) {
            flag |= IS_NATURAL_SORTED;
        }
        if ((characteristics & Spliterator.SIZED) == Spliterator.SIZED) {
            flag |= IS_SIZED;
        }
        if ((characteristics & Spliterator.NONNULL) == Spliterator.NONNULL) {
            flag |= IS_NONNULL;
        }
        return flag;
    }
}
