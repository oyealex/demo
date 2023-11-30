package com.oye.common.bitset;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * 简单的位序列枚举工具。
 * <p/>
 * 支持将枚举类序列化为位的工具。
 * <br/>
 * 能够将相关的枚举值序列化为位序列，或者从位序列中反序列化枚举，并提供了一些常用枚举处理功能。
 * <br/>
 * 使用基本类型{@code long}作为位序列实现，仅支持不超过64个枚举值。
 *
 * @param <E> 枚举实际类型。
 * @author oyealex
 * @since 2023-11-30
 */
public class SimpleBitEnumTool<E extends Enum<E>> {
    private static final int MAX_ENUM_VALUE_COUNT = Long.SIZE;

    /** 枚举类型。 */
    private final Class<E> enumType;

    /** 枚举值到位索引的映射。 */
    private final EnumMap<E, Integer> bitIndexMap;

    /** 索引到枚举值的映射。 */
    private final Object[] enumArray;

    /** 有效的位掩码。 */
    private final long bitsMask;

    /**
     * 从给定的枚举类型中构造位序列枚举工具对象，以枚举值的{@linkplain Enum#ordinal() 默认序列}作为位索引。
     *
     * @param enumType 枚举类型。
     * @throws IllegalArgumentException 当给定的类型不是有效枚举类型，或者含有超过最大支持数量的枚举值时抛出。
     * @throws NullPointerException 当给定的类型为{@code null}时抛出。
     */
    public SimpleBitEnumTool(Class<E> enumType) {
        if (!requireNonNull(enumType).isEnum()) {
            throw new IllegalArgumentException("invalid enum type: " + enumType);
        }
        E[] enums = enumType.getEnumConstants();
        if (enums.length > MAX_ENUM_VALUE_COUNT) {
            throw new IllegalArgumentException("unsupported enum type: " + enumType + ", it contains " + enums.length +
                " enum values which exceeds the supported upper limit: " + MAX_ENUM_VALUE_COUNT);
        }
        long mask = 0L;
        EnumMap<E, Integer> map = new EnumMap<>(enumType);
        for (E enumValue : enums) {
            map.put(enumValue, enumValue.ordinal());
            mask |= 1L << enumValue.ordinal();
        }
        this.enumType = enumType;
        this.bitIndexMap = map;
        this.enumArray = enums;
        this.bitsMask = mask;
    }

    /**
     * 从给定的枚举值容器中构造位序列枚举工具对象，以给定枚举值在容器迭代器中出现的次序为位索引。
     *
     * @param enums 给定的枚举值容器。
     * @throws IllegalArgumentException 当给定的枚举值容器为空、容器值不是有效枚举类型、枚举值类型不一致
     * 或者含有的唯一枚举值数量超过最大支持数量时抛出。
     * @throws NullPointerException 当给定的容器为{@code null}时抛出。
     */
    @SuppressWarnings("unchecked")
    public SimpleBitEnumTool(Collection<E> enums) {
        requireNonNull(enums);
        if (enums.isEmpty()) {
            throw new IllegalArgumentException("no enum value found");
        }
        int index = 0;
        long mask = 1L; // the first one must exist
        Iterator<E> iterator = enums.iterator();
        E first = iterator.next();
        Class<E> type = (Class<E>) first.getClass();
        Object[] array = new Object[MAX_ENUM_VALUE_COUNT];
        array[index] = first;
        EnumMap<E, Integer> map = new EnumMap<>(type);
        map.put(first, index++);
        while (iterator.hasNext()) {
            E enumValue = iterator.next();
            // check if type is consistent
            if (!type.equals(enumValue.getClass())) {
                throw new IllegalArgumentException("inconsistent enum types: " + type + " vs " + enumValue.getClass());
            }
            if (map.containsKey(enumValue)) {
                // existed
                continue;
            }
            mask |= 1L << index;
            array[index] = enumValue;
            map.put(enumValue, index++);
        }
        this.enumType = type;
        this.bitIndexMap = map;
        this.enumArray = index == MAX_ENUM_VALUE_COUNT ? array : Arrays.copyOf(array, index);
        this.bitsMask = mask;
    }

    /**
     * 将给定的枚举值序列化为位序列。
     * <p/>
     * 如果其中的值不被此工具管理，则该值被忽略。
     *
     * @param enums 需要转换为位序列的枚举值。
     * @return 位序列。
     */
    @SafeVarargs
    public final long toBits(E... enums) {
        long bits = 0L;
        for (E enumValue : enums) {
            int index = bitIndexMap.getOrDefault(enumValue, -1);
            if (index >= 0) {
                bits |= 1L << index;
            }
        }
        return bits;
    }

    /**
     * 将给定容器中的枚举值序列化为位序列。
     * <p/>
     * 如果给定的容器为{@code null}或不含任何值，则返回{@code 0L}，如果容器中的值不被此工具管理，则该值被忽略。
     *
     * @param enums 含有枚举值的容器。
     * @return 位序列。
     */
    public long toBits(Collection<E> enums) {
        if (enums == null || enums.isEmpty()) {
            return 0L;
        }
        long bits = 0L;
        for (E enumValue : enums) {
            int index = bitIndexMap.getOrDefault(enumValue, -1);
            if (index >= 0) {
                bits |= 1L << index;
            }
        }
        return bits;
    }

    /**
     * 从给定的位序列中反序列化枚举值。
     *
     * @param bits 位序列。
     * @return 枚举值集合。
     */
    @SuppressWarnings("unchecked")
    public Set<E> fromBits(long bits) {
        if (bits == 0L) {
            return emptySet();
        }
        EnumSet<E> set = EnumSet.noneOf(enumType);
        long remain = bits & bitsMask;
        for (int index = 0; remain != 0L && index < enumArray.length; remain >>>= 1, index++) {
            if ((remain & 1) == 1) {
                set.add((E) enumArray[index]);
            }
        }
        return set;
    }

    /**
     * 获取给定枚举的位索引。
     *
     * @param enumValue 枚举值。
     * @return 给定枚举的位索引。
     * @throws NullPointerException 当给定的枚举值{@code enumValue}为{@code null}或未被此工具管理时抛出。
     */
    public int getBitIndex(E enumValue) {
        return bitIndexMap.get(requireNonNull(enumValue));
    }

    /**
     * 根据位序列判断代表的两个枚举序列是否含有交集。
     *
     * @param firstBits 第一份枚举序列的位序列。
     * @param secondBits 第二份枚举序列的位序列。
     * @return {@code true} - 给定的两个枚举序列含有交集。
     */
    public boolean haveIntersections(long firstBits, long secondBits) {
        return (bitsMask & firstBits & secondBits) != 0L;
    }

    /**
     * 获取给定两个枚举序列交集数量。
     *
     * @param firstBits 第一份枚举序列的位序列。
     * @param secondBits 第二份枚举序列的位序列。
     * @return 给定两个枚举序列交集数量。
     */
    public int countIntersections(long firstBits, long secondBits) {
        return Long.bitCount(bitsMask & firstBits & secondBits);
    }

    /**
     * 获取给定两个枚举序列交集。
     *
     * @param firstBits 第一份枚举序列的位序列。
     * @param secondBits 第二份枚举序列的位序列。
     * @return 给定两个枚举序列交集。
     */
    public Set<E> getIntersections(long firstBits, long secondBits) {
        return fromBits(bitsMask & firstBits & secondBits);
    }

    /**
     * 获取所有被此工具管理的枚举值。
     * <p/>
     * 对返回的集合进行的修改不影响此工具管理的枚举值。
     *
     * @return 所有被此工具管理的枚举值集合。
     */
    public Set<E> getAllEnums() {
        // 避免暴露修改接口
        return EnumSet.copyOf(bitIndexMap.keySet());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + enumType.getSimpleName() + ":" + bitIndexMap.keySet();
    }
}
