package com.oyealex.pipe.flag;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * 流水线标记，用于辅助对流水线的优化。
 *
 * @author oyealex
 * @since 2023-05-05
 */
public enum PipeFlag {
    /** 所有数据基于{@link Object#equals(Object)}比较规则判定为唯一 */
    DISTINCT(0),

    /** 所有数据均实现了{@link Comparable}并按照自然顺序排序 */
    SORTED(1),

    /** 所有数据均实现了{@link Comparable}并按照自然顺序逆序排序 */
    REVERSED_SORTED(2),

    /** 数据源是有限的 */
    SIZED(3),

    /** 所有数据都不为{@code null} */
    NONNULL(4),

    /** 流水线操作可以被短路 */
    SHORT_CIRCUIT(5),
    ;

    public static final int IS_DISTINCT = DISTINCT.setBit;

    public static final int NOT_DISTINCT = DISTINCT.clearBit;

    public static final int IS_SORTED = SORTED.setBit;

    public static final int NOT_SORTED = SORTED.clearBit;

    public static final int IS_REVERSED_SORTED = REVERSED_SORTED.setBit;

    public static final int NOT_REVERSED_SORTED = REVERSED_SORTED.clearBit;

    public static final int IS_SIZED = SIZED.setBit;

    public static final int NOT_SIZED = SIZED.clearBit;

    public static final int IS_NONNULL = NONNULL.setBit;

    public static final int NOT_NONNULL = NONNULL.clearBit;

    public static final int IS_SHORT_CIRCUIT = SHORT_CIRCUIT.setBit;

    public static final int NOT_SHORT_CIRCUIT = SHORT_CIRCUIT.clearBit;

    private static final int SET_BIT = 0B01;

    private static final int CLEAR_BIT = 0B10;

    private static final int KEEP_BIT = 0B11;

    private static final int ALL_SET_BIT = stream(values()).mapToInt(flag -> flag.setBit)
        .reduce(0, (value, flagBit) -> value | flagBit);

    private static final int ALL_CLEAR_BIT = stream(values()).mapToInt(flag -> flag.clearBit)
        .reduce(0, (value, flagBit) -> value | flagBit);

    private final int setBit;

    private final int clearBit;

    private final int keepBit;

    PipeFlag(int offset) {
        int bitOffset = offset * 2;
        this.setBit = SET_BIT << bitOffset;
        this.clearBit = CLEAR_BIT << bitOffset;
        this.keepBit = KEEP_BIT << bitOffset;
    }

    /**
     * 将给定的{@code opFlag}结合到{@code flag}，形成一个新的标记。
     * <p/>
     * 一个例子说明结合步骤：
     * <pre><code>
     * ┏━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
     * ┃ Variable Name ┃    Flag Binary    ┃                        Comment                         ┃
     * ┡━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┩
     * │ flag          │ 01 01 00 00 10 10 │ use op flag bit to cover flag bit, but keep other      │
     * │ opFlag        │ 00 10 00 00 00 01 │ flag bit                                               │
     * │ result flag   │ 01 10 00 00 10 01 │                                                        │
     * ├───────────────┼───────────────────┼────────────────────────────────────────────────────────┤
     * │ movedSetBit   │ 00 00 00 00 00 01 │ find set bit and move: (opFlag & ALL_SET_BIT) << 1     │
     * │ movedClearBit │ 00 10 00 00 00 00 │ find clear bit and move: (opFlag & ALL_CLEAR_BIT) >> 1 │
     * │ changedMask   │ 00 11 00 00 00 11 │ get change bit mask: movedSetBit | movedClearBit       │
     * │ unchangedMask │ 11 00 11 11 11 00 │ get unchanged bit mask: ~changedMask                   │
     * │ unchangedFlag │ 01 00 00 00 10 00 │ get unchanged flag bit: flag & unchangedMask           │
     * │ resultFlag    │ 01 10 00 00 10 01 │ get result: unchangedFlag | opFlag                     │
     * └───────────────┴───────────────────┴────────────────────────────────────────────────────────┘
     * </code></pre>
     *
     * @param flag 原标记
     * @param opFlag 需要结合的标记
     * @return 结合之后的标记
     */
    public static int combine(int flag, int opFlag) {
        return flag & ~((opFlag & ALL_SET_BIT) << 1 | (opFlag & ALL_CLEAR_BIT) >> 1) | opFlag;
    }

    /**
     * 判断是否设置此标记
     *
     * @param flag 标记值
     * @return 是否设置此标记
     */
    public boolean isSet(int flag) {
        return (flag & keepBit) == setBit;
    }

    /**
     * 判断是否清除此标记
     *
     * @param flag 标记值
     * @return 是否清除此标记
     */
    public boolean isCleared(int flag) {
        return (flag & keepBit) == clearBit;
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
            flag |= IS_DISTINCT;
        }
        if ((characteristics & Spliterator.SORTED) == Spliterator.SORTED && spliterator.getComparator() == null) {
            flag |= IS_SORTED;
        }
        if ((characteristics & Spliterator.SIZED) == Spliterator.SIZED) {
            flag |= IS_SIZED;
        }
        if ((characteristics & Spliterator.NONNULL) == Spliterator.NONNULL) {
            flag |= IS_NONNULL;
        }
        return flag;
    }

    static List<String> readable(int flagValue) {
        return stream(values()).map(flag -> flag.isSet(flagValue) ? "IS_" + flag.name() :
            flag.isCleared(flagValue) ? "NOT_" + flag.name() : null).filter(Objects::nonNull).collect(toList());
    }
}
