/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 时间格式化
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/1/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeFormat {
    private static final TimeUnit[] UNITS = {DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS};

    /**
     * 格式化为{@code 999.23:59:59.999.999}的格式
     *
     * @param nanos 经过的纳秒时间
     * @return 格式化字符串
     */
    public static String fullFormat(long nanos) {
        Long[] values = new Long[UNITS.length];
        for (int i = 0; i < UNITS.length; i++) {
            values[i] = UNITS[i].convert(nanos, NANOSECONDS);
            nanos -= UNITS[i].toNanos(values[i]);
        }
        final String format = "%" + getLog10Count(values[0]) + "d.%02d:%02d:%02d.%03d.%03d";
        return String.format(Locale.ENGLISH, format, (Object[]) values);
    }

    /**
     * 获取该纳秒时间段格式化为长格式需要的字符长度
     *
     * @param nanos 纳秒时间段
     * @return 长格式需要的字符长度
     */
    public static int getFullFormatLength(long nanos) {
        return getLog10Count(NANOSECONDS.toDays(nanos)) + 17;
    }

    public static int getAdaptiveFormatLength(long nanos) {
        long days = NANOSECONDS.toDays(nanos);
        if (days > 0) {
            return getLog10Count(days) + 17;
        } else if (NANOSECONDS.toHours(nanos) > 0) {
            return 16;
        } else if (NANOSECONDS.toMinutes(nanos) > 0) {
            return 13;
        } else {
            return 10;
        }
    }

    public static String adaptiveFormat(long nanos) {
        final String fullFormat = fullFormat(nanos);
        return fullFormat.substring(fullFormat.length() - getAdaptiveFormatLength(nanos));
    }

    private static int getLog10Count(double value) {
        return Math.max(1, (int) Math.ceil(Math.log10(value)));
    }
}
