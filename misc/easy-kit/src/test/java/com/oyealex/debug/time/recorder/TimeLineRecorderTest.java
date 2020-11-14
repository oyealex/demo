/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time.recorder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TimeLineRecorderTest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/1/20
 */
class TimeLineRecorderTest {
    @AfterEach
    void clean() {
        TimeLineRecorder.global().clear();
    }

    @Test
    void summaryFormat_smoke() {
        monitorMultiThreadRun();
        System.out.println(TimeLineRecorder.global().formatStatistics());
        System.out.println();
        System.out.println(String.join("\n\n", TimeLineRecorder.global().formatTimeLines(null, null)));
        assertTrue(true);
    }

    private void monitorMultiThreadRun() {
        final List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(ignored -> new Thread(this::monitorRandomSleepThread)).collect(Collectors.toList());
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void monitorRandomSleepThread() {
        TimeLineRecorder.global().start("smoke");
        sleepRandom();
        TimeLineRecorder.global().record("record 1");
        sleepRandom();
        TimeLineRecorder.global().record("record 2");
        sleepRandom();
        TimeLineRecorder.global().recordAnonymous();
        sleepRandom();
        TimeLineRecorder.global().record("recordWithLongName");
        TimeLineRecorder.global().stop();
    }

    private void sleepRandom() {
        try {
            Thread.sleep((long) (Math.random() * 100L));
        } catch (InterruptedException ignored) {
        }
    }
}