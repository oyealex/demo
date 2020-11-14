/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time;

import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * TimeFormatTest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/1/20
 */
class TimeFormatTest {
    @Test
    void fullFormat() {
        String[] expected = new String[]{
            "1.00:00:00.000.000",
            "999.00:00:00.000.000",
            "0.01:00:00.000.000",
            "0.23:00:00.000.000",
            "0.00:01:00.000.000",
            "0.00:59:00.000.000",
            "0.00:00:01.000.000",
            "0.00:00:59.000.000",
            "0.00:00:00.001.000",
            "0.00:00:00.999.000",
            "0.00:00:00.000.001",
            "0.00:00:00.000.999",
            "999.23:59:59.999.999"
        };
        String[] results = new String[]{
            TimeFormat.fullFormat(DAYS.toNanos(1)),
            TimeFormat.fullFormat(DAYS.toNanos(999)),
            TimeFormat.fullFormat(HOURS.toNanos(1)),
            TimeFormat.fullFormat(HOURS.toNanos(23)),
            TimeFormat.fullFormat(MINUTES.toNanos(1)),
            TimeFormat.fullFormat(MINUTES.toNanos(59)),
            TimeFormat.fullFormat(SECONDS.toNanos(1)),
            TimeFormat.fullFormat(SECONDS.toNanos(59)),
            TimeFormat.fullFormat(MILLISECONDS.toNanos(1)),
            TimeFormat.fullFormat(MILLISECONDS.toNanos(999)),
            TimeFormat.fullFormat(MICROSECONDS.toNanos(1)),
            TimeFormat.fullFormat(MICROSECONDS.toNanos(999)),
            TimeFormat.fullFormat(
                DAYS.toNanos(999) +
                    HOURS.toNanos(23) +
                    MINUTES.toNanos(59) +
                    SECONDS.toNanos(59) +
                    MILLISECONDS.toNanos(999) +
                    MICROSECONDS.toNanos(999))
        };
        assertArrayEquals(expected, results);
    }

    @Test
    void adaptiveFormat() {
        String[] expected = new String[]{
            "1.00:00:00.000.000",
            "999.00:00:00.000.000",
            "01:00:00.000.000",
            "23:00:00.000.000",
            "01:00.000.000",
            "59:00.000.000",
            "01.000.000",
            "59.000.000",
            "00.001.000",
            "00.999.000",
            "00.000.001",
            "00.000.999",
            "999.23:59:59.999.999"
        };
        String[] results = new String[]{
            TimeFormat.adaptiveFormat(DAYS.toNanos(1)),
            TimeFormat.adaptiveFormat(DAYS.toNanos(999)),
            TimeFormat.adaptiveFormat(HOURS.toNanos(1)),
            TimeFormat.adaptiveFormat(HOURS.toNanos(23)),
            TimeFormat.adaptiveFormat(MINUTES.toNanos(1)),
            TimeFormat.adaptiveFormat(MINUTES.toNanos(59)),
            TimeFormat.adaptiveFormat(SECONDS.toNanos(1)),
            TimeFormat.adaptiveFormat(SECONDS.toNanos(59)),
            TimeFormat.adaptiveFormat(MILLISECONDS.toNanos(1)),
            TimeFormat.adaptiveFormat(MILLISECONDS.toNanos(999)),
            TimeFormat.adaptiveFormat(MICROSECONDS.toNanos(1)),
            TimeFormat.adaptiveFormat(MICROSECONDS.toNanos(999)),
            TimeFormat.fullFormat(
                DAYS.toNanos(999) +
                    HOURS.toNanos(23) +
                    MINUTES.toNanos(59) +
                    SECONDS.toNanos(59) +
                    MILLISECONDS.toNanos(999) +
                    MICROSECONDS.toNanos(999))
        };
        assertArrayEquals(expected, results);
    }
}