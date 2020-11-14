/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.thread;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;

/**
 * 线程工具类
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/10/19
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Threads {
    /**
     * 等待指定毫秒数，不响应中断
     *
     * @param sleepMillis 等待时间，毫秒
     * @return true - 等待结束； false - 等待中断
     */
    public static boolean sleepUninterruptedly(long sleepMillis) {
        try {
            sleep(sleepMillis);
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    /**
     * 等待指定时间，不响应中断
     *
     * @param time 时间
     * @param unit 单位
     * @return true - 等待结束； false - 等待中断
     */
    public static boolean sleepUninterruptedly(long time, @NonNull TimeUnit unit) {
        return sleepUninterruptedly(unit.toMillis(time));
    }

    /**
     * 等待指定范围内的随机时间，不响应中断
     *
     * @param minMillis 最小等待毫秒时间
     * @param maxMillis 最大等待毫秒时间
     * @return true - 等待结束； false - 等待中断
     */
    public static boolean sleepRandomTimeUninterruptedly(long minMillis, long maxMillis) {
        return sleepUninterruptedly(minMillis + (long) (Math.random() * (maxMillis - minMillis)));
    }

    /**
     * 等待指定范围内的随机时间，不响应中断
     *
     * @param minMillis 最小等待毫秒时间
     * @param maxMillis 最大等待毫秒时间
     * @throws InterruptedException 等待中断
     */
    public static void sleepRandomTime(long minMillis, long maxMillis) throws InterruptedException {
        final long sleepMillis = minMillis + (long) (Math.random() * (maxMillis - minMillis));
        sleep(sleepMillis);
    }

    /**
     * 等待指定毫秒数，当中断时采取指定动作
     *
     * @param sleepMillis         等待毫秒数
     * @param interruptedConsumer 中断时采取的动作
     * @return true - 等待结束； false - 等待中断
     */
    public static boolean sleepAndActionOnInterrupted(long sleepMillis,
        @NonNull Consumer<InterruptedException> interruptedConsumer) {
        try {
            sleep(sleepMillis);
            return true;
        } catch (InterruptedException interruptedException) {
            interruptedConsumer.accept(interruptedException);
            return false;
        }
    }
}
