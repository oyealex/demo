package com.oyealex.pipe.flag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public enum PipeFlag { // TODO 2023-05-20 01:02 重新审视所有标记使用，尤其是IS_NONNULL
    /**
     * 标记流水线中的数据基于{@link Object#equals(Object)}比较规则判定是否唯一
     *
     * @see Spliterator#DISTINCT
     */
    DISTINCT(0),

    /**
     * 标记流水线中的数据是否实现了{@link Comparable}并按照自然顺序排序
     *
     * @see Spliterator#SORTED
     */
    SORTED(1),

    // offset = 2 for Spliterator.ORDERED

    /**
     * 标记原始数据源的数据数量是否准确
     *
     * @see Spliterator#SIZED
     */
    SIZED(3),

    /**
     * 标记流水线中的数据是否不为{@code null}
     *
     * @see Spliterator#NONNULL
     */
    NONNULL(4),

    // offset = 5 for Spliterator.IMMUTABLE
    // offset = 6 for Spliterator.CONCURRENT
    // offset = 7 for Spliterator.SUBSIZED

    /** 标记流水线操作是否可以短路求值 */
    SHORT_CIRCUIT(12),

    /** 标记流水线中数据是否实现了{@link Comparable}并按照自然顺序逆序排序 */
    REVERSED_SORTED(13),
    ;

    /** 空的标记，不改变标记现状 */
    public static final int EMPTY = 0;

    /** 数据非唯一 */
    public static final int IS_DISTINCT = DISTINCT.setBit;

    /** 数据唯一 */
    public static final int NOT_DISTINCT = DISTINCT.clearBit;

    /** 数据自然有序 */
    public static final int IS_SORTED = SORTED.setBit;

    /** 数据非自然有序 */
    public static final int NOT_SORTED = SORTED.clearBit;

    /** 数据数据有界 */
    public static final int IS_SIZED = SIZED.setBit;

    public static final int NOT_SIZED = SIZED.clearBit;

    public static final int IS_NONNULL = NONNULL.setBit;

    public static final int NOT_NONNULL = NONNULL.clearBit;

    public static final int IS_SHORT_CIRCUIT = SHORT_CIRCUIT.setBit;

    // there is no NOT_SHORT_CIRCUIT

    public static final int IS_REVERSED_SORTED = REVERSED_SORTED.setBit;

    public static final int NOT_REVERSED_SORTED = REVERSED_SORTED.clearBit;

    public static final int SPLIT_MASK = Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED |
        Spliterator.NONNULL;

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
        int flag = characteristics & SPLIT_MASK;
        if ((characteristics & Spliterator.SORTED) == Spliterator.SORTED && spliterator.getComparator() != null) {
            flag &= ~Spliterator.SORTED;
            flag |= NOT_SORTED;
        }
        return flag;
    }

    public static int toSpliteratorFlag(int pipeFlag) {
        return pipeFlag & SPLIT_MASK;
    }

    static List<String> toReadablePipeFlag(int flagValue) {
        return stream(values()).map(flag -> flag.isSet(flagValue) ? "IS_" + flag.name() :
            flag.isCleared(flagValue) ? "NOT_" + flag.name() : null).filter(Objects::nonNull).collect(toList());
    }

    private static final Map<Integer, String> SPLITERATOR_CHARACTERISTICS_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(Spliterator.ORDERED, "ORDERED");
        map.put(Spliterator.DISTINCT, "DISTINCT");
        map.put(Spliterator.SORTED, "SORTED");
        map.put(Spliterator.SIZED, "SIZED");
        map.put(Spliterator.NONNULL, "NONNULL");
        map.put(Spliterator.IMMUTABLE, "IMMUTABLE");
        map.put(Spliterator.CONCURRENT, "CONCURRENT");
        map.put(Spliterator.SUBSIZED, "SUBSIZED");
        SPLITERATOR_CHARACTERISTICS_MAP = Collections.unmodifiableMap(map);
    }

    static List<String> toReadableSplitCharacteristics(int flagVale) {
        return SPLITERATOR_CHARACTERISTICS_MAP.entrySet()
            .stream()
            .map(entry -> (flagVale & entry.getKey()) == entry.getKey() ? entry.getValue() : null)
            .filter(Objects::nonNull)
            .collect(toList());
    }
}
