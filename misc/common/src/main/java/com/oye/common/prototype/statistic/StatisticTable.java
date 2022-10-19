package com.oye.common.prototype.statistic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计表格
 * <p/>
 * 用于支持任意维度的常规数据统计任务。
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-10-19
 */
@NotThreadSafe
public class StatisticTable {
    /** 统计方法 */
    @Getter
    private final StatisticMethod statisticMethod;

    /** 统计项的默认值 */
    @Getter
    private final long statisticDefaultValue;

    /** 统计维度值为空时的默认值 */
    @Getter
    private final String nullDimensionValue;

    /** 统计项表格 */
    private final Map<String, StatisticValue> table = new HashMap<>();

    public StatisticTable(StatisticMethod statisticMethod, long statisticDefaultValue, String nullDimensionValue) {
        this.statisticMethod = statisticMethod;
        this.statisticDefaultValue = statisticDefaultValue;
        this.nullDimensionValue = nullDimensionValue;
    }

    public StatisticTable(StatisticMethod statisticMethod) {
        this(statisticMethod, 0L, "null");
    }

    /**
     * 按照给定的方法统计给定值
     *
     * @param value       待统计的值
     * @param coordinates 统计项的坐标描述
     */
    public void statistic(long value, Object... coordinates) {
        statisticMethod.calcValue.accept(
                table.computeIfAbsent(getKey(coordinates),
                        key -> new StatisticValue(statisticDefaultValue, coordinates)), value);
    }

    /**
     * 获取统计结果
     *
     * @return 最终统计结果
     */
    public List<StatisticItem> getResult() {
        return table.values().stream()
                .map(value -> new StatisticItem(buildStringCoordinates(value.coordinates),
                        statisticMethod.calcResult.apply(value)))
                .collect(Collectors.toList());
    }

    private String[] buildStringCoordinates(Object[] coordinates) {
        return Arrays.stream(coordinates).map(this::getStringCoordinate).toArray(String[]::new);
    }

    private String getKey(Object[] coordinates) {
        StringBuilder keyBuilder = new StringBuilder();
        for (Object coordinate : coordinates) {
            keyBuilder.append(getStringCoordinate(coordinate)).append("\u0000");
        }
        return keyBuilder.toString();
    }

    private String getStringCoordinate(Object coordinate) {
        return coordinate == null ? nullDimensionValue : coordinate.toString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum StatisticMethod {
        /** 计数 */
        COUNT((item, value) -> item.count(), StatisticValue::getCount),
        /** 取平均 */
        AVERAGE(StatisticValue::add, StatisticValue::getAverage),
        /** 求和 */
        SUM(StatisticValue::add, StatisticValue::getValue),
        /** 最大值 */
        MAX(StatisticValue::max, StatisticValue::getValue),
        /** 最小值 */
        MIN(StatisticValue::min, StatisticValue::getValue),
        ;

        /** 统计值计算方法 */
        private final BiConsumer<StatisticValue, Long> calcValue;

        /** 结果获取方法 */
        private final Function<StatisticValue, Number> calcResult;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StatisticItem {
        /** 统计项目坐标 */
        private final String[] coordinates;

        /** 统计值结果 */
        private final Number value;
    }

    /** 用于统计值计算等相关操作 */
    @Getter
    @ToString
    @NotThreadSafe
    private static class StatisticValue {
        private final Object[] coordinates;

        /** 统计值 */
        private long value;

        /** 统计计算次数 */
        private int count = 0;

        private StatisticValue(long defaultValue, Object... coordinates) {
            this.value = defaultValue;
            this.coordinates = coordinates;
        }

        /**
         * 不更新统计值，只增加计算次数
         */
        public void count() {
            count++;
        }

        /**
         * 累加给定值
         *
         * @param addValue 需要累加的值，可以为负值
         */
        public void add(long addValue) {
            value += addValue;
            count++;
        }

        /**
         * 更新统计值为当前值与给定值中较大者
         *
         * @param anotherValue 用于比较的给定值
         */
        public void max(long anotherValue) {
            value = Math.max(value, anotherValue);
            count++;
        }

        /**
         * 更新统计值为当前值与给定值中较小者
         *
         * @param anotherValue 用于比较的给定值
         */
        public void min(long anotherValue) {
            value = Math.min(value, anotherValue);
            count++;
        }

        /**
         * 计算当前统计值的平均值
         *
         * @return 当前统计值的平均值，如果操作次数为0，则返回{@link Double#NaN}
         */
        public double getAverage() {
            return count == 0L ? Double.NaN : 1.0 * value / count;
        }
    }
}
