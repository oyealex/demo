/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time.recorder;

import com.oyealex.debug.time.TimeFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * 时间线记录器的格式化类，负责其格式化工作，避免增加其他类的复杂度
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Formatter {
    static final DecimalFormat DOUBLE_COMMA_FORMAT = new DecimalFormat(",##0.000");

    static final DecimalFormat LONG_COMMA_FORMAT = new DecimalFormat(",###");

    private static final double MILLIS = 1_000_000.0D;

    /** 获取给定正整数值格式化之后的字符长度 */
    private static int getDigitsSizeWithComma(long value) {
        int digitsSize = getDigitsSize(value);
        return digitsSize + digitsSize / 3 + (digitsSize % 3 == 0 ? -1 : 0);
    }

    /** 获取给定值的数字个数，参考{@code Long.stringSize(long)} */
    private static int getDigitsSize(long value) {
        int d = 1;
        if (value >= 0) {
            d = 0;
            value = -value;
        }
        long p = -10;
        for (int i = 1; i < 19; i++) {
            if (value > p)
                return i + d;
            p = 10 * p;
        }
        return 19 + d;
    }

    /**
     * 按照给定条件过滤匹配的时间线记录，执行统计并格式化
     *
     * @param recorder     时间线记录器
     * @param timeLineName 时间线名称，null表示不过滤
     * @param startThread  时间线开始线程名称，null表示不过滤
     * @return 可读字符串
     */
    static String formatStatistics(TimeLineRecorder recorder, String timeLineName,
        String startThread) {
        long watch = System.nanoTime();
        final Stream<TimeLine> timeLineStream = recorder.getTimeLines().stream()
            .filter(timeLine -> timeLine.match(timeLineName, startThread));
        String summaryResult = formatSummary(statistics(timeLineStream));
        watch = System.nanoTime() - watch;
        return "Summary Cost: " + DOUBLE_COMMA_FORMAT.format(watch / MILLIS) + "ms\n" + summaryResult;
    }

    private static Map<String, Statistics> statistics(Stream<TimeLine> timeLineStream) {
        return timeLineStream
            .map(TimeLine::getStampRecords)
            .flatMap(Collection::stream)
            // .filter(StampRecord::isNamedRecord)
            .collect(groupingBy(StampRecord::getName, LinkedHashMap::new,
                collectingAndThen(toList(), Formatter::statisticsToSummary)));
    }

    private static String formatSummary(Map<String, Statistics> recordNameToSummary) {
        if (recordNameToSummary.isEmpty()) {
            return "";
        }
        final Set<Map.Entry<String, Statistics>> entries = recordNameToSummary.entrySet();
        int noFormatLength = getDigitsSize(recordNameToSummary.size());
        int nameFormatLength = 0;
        int countFormatLength = 0;
        int avgFormatLength = 0;
        int sumFormatLength = 0;
        int minFormatLength = 0;
        int maxFormatLength = 0;
        for (Map.Entry<String, Statistics> entry : entries) {
            Statistics value = entry.getValue();
            nameFormatLength = max(nameFormatLength, entry.getKey().length());
            countFormatLength = max(countFormatLength, getDigitsSizeWithComma(value.count));
            avgFormatLength = Math.max(avgFormatLength, TimeFormat.getAdaptiveFormatLength((long) value.average));
            sumFormatLength = Math.max(sumFormatLength, TimeFormat.getAdaptiveFormatLength(value.sum));
            minFormatLength = Math.max(minFormatLength, TimeFormat.getAdaptiveFormatLength(value.minimum));
            maxFormatLength = Math.max(maxFormatLength, TimeFormat.getAdaptiveFormatLength(value.maximum));
        }
        String pattern = "%0" + noFormatLength + "d # %-" + nameFormatLength + "s : %" + avgFormatLength + "s * %" +
            countFormatLength + "s = %" + sumFormatLength + "s [%" + minFormatLength + "s, %" +
            maxFormatLength + "s] %s";
        int[] no = new int[1];
        return entries.stream().map(entry -> formatStatisticsEntry(entry, no[0]++, pattern)).collect(joining("\n"));
    }

    private static String formatStatisticsEntry(Map.Entry<String, Statistics> entry, int no,
        String pattern) {
        final Statistics summary = entry.getValue();
        return String.format(Locale.ENGLISH, pattern,
            no,
            entry.getKey(),
            TimeFormat.adaptiveFormat((long) summary.average),
            LONG_COMMA_FORMAT.format(summary.count),
            TimeFormat.adaptiveFormat(summary.sum),
            TimeFormat.adaptiveFormat(summary.minimum),
            TimeFormat.adaptiveFormat(summary.maximum),
            DOUBLE_COMMA_FORMAT.format(summary.stdVariance));
    }

    private static Statistics statisticsToSummary(List<StampRecord> records) {
        Statistics statistics = new Statistics();
        if (records.isEmpty()) {
            statistics.minimum = 0L;
            return statistics;
        }
        double squareSum = 0.0;
        for (StampRecord record : records) {
            long elapsed = record.getElapsed();
            statistics.sum += elapsed;
            statistics.count++;
            statistics.minimum = min(statistics.minimum, elapsed);
            statistics.maximum = max(statistics.maximum, elapsed);
            squareSum += 1.0 * elapsed * elapsed;
        }
        statistics.average = 1.0 * statistics.sum / statistics.count;
        statistics.stdVariance = sqrt((squareSum / statistics.count -
            1.0 * statistics.sum * statistics.sum / (statistics.count * statistics.count)) / 1_000_000_000_000.0);
        return statistics;
    }

    public static List<String> formatTimeLines(TimeLineRecorder timeLineRecorder, String timeLineName,
        String startThread) {
        return timeLineRecorder.getTimeLines()
            .stream()
            .filter(timeLine -> timeLine.match(timeLineName, startThread))
            .map(Formatter::formatStampRecords)
            .collect(toList());
    }

    static String formatStampRecords(TimeLine timeLine) {
        final LinkedList<StampRecord> records = timeLine.getStampRecords();
        int noFormatLength = getDigitsSize(records.size());
        int nameFormatLength = 0;
        int elapsedFormatLength = 0;
        for (StampRecord record : records) {
            // if (record.isNamedRecord()) {
            nameFormatLength = max(nameFormatLength, record.getName().length());
            elapsedFormatLength = Math
                .max(elapsedFormatLength, TimeFormat.getAdaptiveFormatLength(record.getElapsed()));
            // }
        }
        String formatPattern = "%" + noFormatLength + "d # %-" + nameFormatLength + "s : %" + elapsedFormatLength + "s";
        int[] no = new int[1];
        return timeLine.getIdentityInfo() + "\n" +
            records.stream()
                // .filter(StampRecord::isNamedRecord)
                .map(record -> String.format(Locale.ENGLISH, formatPattern,
                    no[0]++,
                    record.getName(),
                    TimeFormat.adaptiveFormat(record.getElapsed())))
                .collect(Collectors.joining("\n"));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Statistics {
        private double average = 0.0;

        private long count = 0L;

        private long sum = 0L;

        private long minimum = Long.MAX_VALUE;

        private long maximum = 0L;

        /** 总体标准差 */
        private double stdVariance = 0.0;
    }
}

