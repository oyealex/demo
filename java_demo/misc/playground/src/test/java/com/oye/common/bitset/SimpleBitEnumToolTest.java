package com.oye.common.bitset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 针对{@link SimpleBitEnumTool}的测试。
 *
 * @author oyealex
 * @since 2023-12-01
 */
class SimpleBitEnumToolTest {
    private static final SimpleBitEnumTool<TimeUnit> TOOL = new SimpleBitEnumTool<>(TimeUnit.class);

    private static final List<TimeUnit> PARTS = List.of(NANOSECONDS, MICROSECONDS, MILLISECONDS);

    private static final SimpleBitEnumTool<TimeUnit> SHORTEN_TOOL = new SimpleBitEnumTool<>(PARTS);

    private static final long INVALID_BITS = 1L << 32;

    private static final long ANOTHER_INVALID_BITS = 1L << 33;

    @Test
    @DisplayName("能够正确从枚举类型中构造枚举位序列工具")
    void should_construct_tool_from_enum_type_rightly() {
        assertAll(
            // all enum values
            () -> assertEquals(EnumSet.allOf(TimeUnit.class), TOOL.getAllEnums()),
            // check enum ordinal
            () -> assertArrayEquals(stream(TimeUnit.values()).mapToInt(Enum::ordinal).toArray(),
                stream(TimeUnit.values()).mapToInt(TOOL::getBitIndex).toArray()));
    }

    @Test
    @DisplayName("能够正确从给定枚举值容器中构造枚举位序列工具")
    void should_construct_tool_from_given_enum_values_rightly() {
        SimpleBitEnumTool<TimeUnit> tool = new SimpleBitEnumTool<>(List.of(SECONDS, SECONDS, DAYS, MINUTES, DAYS));
        assertAll(
            // distinct enum values
            () -> assertEquals(EnumSet.of(SECONDS, DAYS, MINUTES), tool.getAllEnums()),
            // check repeat enum value's index
            () -> assertEquals(0, tool.getBitIndex(SECONDS)), () -> assertEquals(1, tool.getBitIndex(DAYS)),
            () -> assertEquals(2, tool.getBitIndex(MINUTES)));
    }

    @Test
    @DisplayName("如果构造参数非法则抛出异常")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void should_throw_exception_when_constructing_param_is_invalid() {
        assertAll(
            // null enum type
            () -> assertThrowsExactly(NullPointerException.class, () -> new SimpleBitEnumTool((Class<?>) null)),
            // not an enum type
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> new SimpleBitEnumTool(String.class)),
            // too many enum values
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> new SimpleBitEnumTool<>(LotsOfEnum.class)),
            // null enum collection
            () -> assertThrowsExactly(NullPointerException.class, () -> new SimpleBitEnumTool<>((List<TimeUnit>) null)),
            // empty enum collection
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> new SimpleBitEnumTool<TimeUnit>(emptyList())),
            // inconsistent enum type
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> new SimpleBitEnumTool(List.<Object>of(SECONDS, LotsOfEnum.E0))));
    }

    @Test
    @DisplayName("能够正确从枚举值数组解析得到位序列")
    void should_get_bits_from_enums_array_rightly() {
        assertAll(
            // empty array
            () -> assertEquals(0L, TOOL.toBits()),
            // normal array
            () -> assertEquals(1L << MINUTES.ordinal() | 1L << DAYS.ordinal() | 1L << HOURS.ordinal(),
                TOOL.toBits(DAYS, HOURS, MINUTES, MINUTES)),
            // not all managed
            () -> assertEquals(1L, SHORTEN_TOOL.toBits(DAYS, HOURS, NANOSECONDS)));
    }

    @Test
    @DisplayName("能够正确从枚举值容器解析得到位序列")
    void should_get_bits_from_enums_collection_rightly() {
        assertAll(
            // null or empty collection
            () -> assertEquals(0L, TOOL.toBits((Collection<TimeUnit>) null)),
            () -> assertEquals(0L, TOOL.toBits(emptyList())),
            // normal collection
            () -> assertEquals(1L << MINUTES.ordinal() | 1L << DAYS.ordinal() | 1L << HOURS.ordinal(),
                TOOL.toBits(List.of(DAYS, HOURS, MINUTES, MINUTES))),
            () -> assertEquals(1L << MINUTES.ordinal() | 1L << DAYS.ordinal() | 1L << HOURS.ordinal(),
                TOOL.toBits(Set.of(DAYS, HOURS, MINUTES))),
            // not all managed
            () -> assertEquals(1L, SHORTEN_TOOL.toBits(List.of(DAYS, HOURS, NANOSECONDS))));
    }

    @Test
    @DisplayName("能够正确从位序列中解析枚举值")
    void should_parse_enums_from_bits_rightly() {
        assertAll(
            // empty
            () -> assertTrue(TOOL.fromBits(0L).isEmpty()),
            // normal bits
            () -> assertEquals(EnumSet.of(DAYS, HOURS), TOOL.fromBits(1L << DAYS.ordinal() | 1L << HOURS.ordinal())),
            // with invalid bits
            () -> assertEquals(EnumSet.allOf(TimeUnit.class), TOOL.fromBits(-1L)));
    }

    @Test
    @DisplayName("能够正确从枚举值解析得到位索引")
    void should_get_bit_index_from_enums_rightly() {
        assertAll(
            // by ordinal
            () -> assertArrayEquals(stream(TimeUnit.values()).mapToInt(Enum::ordinal).toArray(),
                stream(TimeUnit.values()).mapToInt(TOOL::getBitIndex).toArray()),
            // by given order
            () -> assertArrayEquals(IntStream.range(0, PARTS.size()).toArray(),
                PARTS.stream().mapToInt(SHORTEN_TOOL::getBitIndex).toArray()),
            // null value
            () -> assertThrowsExactly(NullPointerException.class, () -> TOOL.getBitIndex(null)),
            // not managed value
            () -> assertThrowsExactly(NullPointerException.class, () -> SHORTEN_TOOL.getBitIndex(HOURS)));
    }

    @Test
    @DisplayName("能够正确判断交集是否存在")
    void should_judge_if_intersection_exist_rightly() {
        assertAll(
            // have
            () -> assertTrue(TOOL.haveIntersection(TOOL.toBits(DAYS, HOURS), TOOL.toBits(DAYS, MINUTES))),
            // not have
            () -> assertFalse(TOOL.haveIntersection(TOOL.toBits(HOURS), TOOL.toBits(MINUTES))),
            () -> assertFalse(TOOL.haveIntersection(0L, 0L)),
            // with invalid bits
            () -> assertTrue(TOOL.haveIntersection(TOOL.toBits(HOURS) | INVALID_BITS, TOOL.toBits(HOURS))),
            () -> assertFalse(TOOL.haveIntersection(INVALID_BITS, ANOTHER_INVALID_BITS | INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确计算交集数量")
    void should_cont_intersection_rightly() {
        assertAll(
            // have
            () -> assertEquals(1, TOOL.countIntersection(TOOL.toBits(DAYS, HOURS), TOOL.toBits(DAYS, MINUTES))),
            () -> assertEquals(2, TOOL.countIntersection(TOOL.toBits(DAYS, HOURS), TOOL.toBits(DAYS, MINUTES, HOURS))),
            // not have
            () -> assertEquals(0, TOOL.countIntersection(TOOL.toBits(HOURS), TOOL.toBits(MINUTES))),
            () -> assertEquals(0, TOOL.countIntersection(0L, 0L)),
            // with invalid bits
            () -> assertEquals(1, TOOL.countIntersection(TOOL.toBits(HOURS) | INVALID_BITS, TOOL.toBits(HOURS))),
            () -> assertEquals(0, TOOL.countIntersection(INVALID_BITS, ANOTHER_INVALID_BITS | INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确获取交集枚举值")
    void should_get_intersection_rightly() {
        assertAll(
            // have
            () -> assertEquals(Set.of(DAYS),
                TOOL.getIntersection(TOOL.toBits(DAYS, HOURS), TOOL.toBits(DAYS, MINUTES))),
            () -> assertEquals(Set.of(DAYS, HOURS),
                TOOL.getIntersection(TOOL.toBits(DAYS, HOURS), TOOL.toBits(DAYS, MINUTES, HOURS))),
            // not have
            () -> assertEquals(Set.of(), TOOL.getIntersection(TOOL.toBits(HOURS), TOOL.toBits(MINUTES))),
            () -> assertEquals(Set.of(), TOOL.getIntersection(0L, 0L)),
            // with invalid bits
            () -> assertEquals(Set.of(HOURS), TOOL.getIntersection(TOOL.toBits(HOURS) | 1L << 32, TOOL.toBits(HOURS))),
            () -> assertEquals(Set.of(), TOOL.getIntersection(INVALID_BITS, 1L << 33 | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确判断差集是否存在")
    void should_judge_if_difference_exist_rightly() {
        assertAll(
            // have
            () -> assertTrue(TOOL.haveDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertFalse(TOOL.haveDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            () -> assertFalse(TOOL.haveDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES, DAYS))),
            // with invalid bits
            () -> assertFalse(TOOL.haveDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertTrue(
                TOOL.haveDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确计算差集数量")
    void should_count_difference_rightly() {
        assertAll(
            // have
            () -> assertEquals(1, TOOL.countDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertEquals(0, TOOL.countDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            () -> assertEquals(0, TOOL.countDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES, DAYS))),
            // with invalid bits
            () -> assertEquals(0, TOOL.countDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertEquals(1,
                TOOL.countDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确获取差集")
    void should_get_difference_rightly() {
        assertAll(
            // have
            () -> assertEquals(Set.of(MINUTES),
                TOOL.getDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertEquals(Set.of(), TOOL.getDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            () -> assertEquals(Set.of(),
                TOOL.getDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES, DAYS))),
            // with invalid bits
            () -> assertEquals(Set.of(), TOOL.getDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertEquals(Set.of(MINUTES),
                TOOL.getDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确判断对称差集是否存在")
    void should_judge_if_symmetric_difference_exist_rightly() {
        assertAll(
            // have
            () -> assertTrue(TOOL.haveSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertFalse(TOOL.haveSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            // with invalid bits
            () -> assertFalse(TOOL.haveSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertTrue(
                TOOL.haveSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确计算对称差集数量")
    void should_count_symmetric_difference_rightly() {
        assertAll(
            // have
            () -> assertEquals(2,
                TOOL.countSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertEquals(0,
                TOOL.countSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            // with invalid bits
            () -> assertEquals(0, TOOL.countSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertEquals(2,
                TOOL.countSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确获取对称差集")
    void should_get_symmetric_difference_rightly() {
        assertAll(
            // have
            () -> assertEquals(Set.of(MINUTES, SECONDS),
                TOOL.getSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, SECONDS))),
            // not have
            () -> assertEquals(Set.of(),
                TOOL.getSymmetricDifference(TOOL.toBits(HOURS, MINUTES), TOOL.toBits(HOURS, MINUTES))),
            // with invalid bits
            () -> assertEquals(Set.of(), TOOL.getSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                TOOL.toBits(HOURS, MINUTES) | ANOTHER_INVALID_BITS)), () -> assertEquals(Set.of(MINUTES, SECONDS),
                TOOL.getSymmetricDifference(TOOL.toBits(HOURS, MINUTES) | INVALID_BITS,
                    TOOL.toBits(HOURS, SECONDS) | ANOTHER_INVALID_BITS)));
    }

    @Test
    @DisplayName("能够正确获取所有被管理的枚举值")
    void should_get_all_managed_enums_rightly() {
        assertAll(
            // all
            () -> assertEquals(EnumSet.allOf(TimeUnit.class), TOOL.getAllEnums()),
            // part
            () -> assertEquals(new HashSet<>(PARTS), SHORTEN_TOOL.getAllEnums()));
    }

    @Test
    @DisplayName("修改获取的所有被管理的枚举值时不影响已经被管理的值")
    void should_not_change_manged_enums_when_modify_result_from_all_enums() {
        TOOL.getAllEnums().clear();
        assertAll(() -> assertEquals(EnumSet.allOf(TimeUnit.class), TOOL.getAllEnums()),
            () -> assertEquals(MINUTES.ordinal(), TOOL.getBitIndex(MINUTES)));
    }

    @Test
    @DisplayName("能正确获取工具对象的字符串表示")
    void should_get_to_string_result_rightly() {
        assertDoesNotThrow(() -> System.out.println(TOOL));
    }

    @SuppressWarnings("unused")
    private enum LotsOfEnum {
        E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16, E17, E18, E19, E20, E21, E22, E23,
        E24, E25, E26, E27, E28, E29, E30, E31, E32, E33, E34, E35, E36, E37, E38, E39, E40, E41, E42, E43, E44, E45,
        E46, E47, E48, E49, E50, E51, E52, E53, E54, E55, E56, E57, E58, E59, E60, E61, E62, E63, E64
    }
}