/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time.recorder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/** 时间线，包含一系列{@link StampRecord}时间戳记录，非线程安全 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
class TimeLine {
    /** 数字ID计数器 */
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private static final int NOT_STARTED = -1;

    private static final int STARTED = 0;

    private static final int STOPPED = 1;

    /** 数字ID */
    @Getter
    @EqualsAndHashCode.Include
    private final int id = ID_COUNTER.getAndIncrement();

    @Getter
    @NonNull
    @EqualsAndHashCode.Include
    private final String name;

    @Getter
    private final String startThreadName = Thread.currentThread().getName();

    @Getter
    private final LinkedList<StampRecord> stampRecords = new LinkedList<>();

    private int state = NOT_STARTED;

    /**
     * 创建并开启一条新的时间线
     *
     * @param name 时间线名称
     * @return 新的已开始的时间线
     */
    public static TimeLine createStarted(String name) {
        final TimeLine timeLine = new TimeLine(name);
        timeLine.start();
        return timeLine;
    }

    /**
     * 开始时间线，每条时间线仅能开始一次
     *
     * @throws IllegalStateException 当前时间线已经启动或已经结束时时抛出
     */
    public void start() {
        requireState(NOT_STARTED);
        stampRecords.add(new StampRecord(StampRecord.START_NAME));
        state = STARTED;
    }

    /**
     * 以给定名称新增一条时间戳记录
     *
     * @param recordName 记录名称
     * @throws IllegalStateException 当前时间线尚未启动或已经结束时时抛出
     */
    public void record(String recordName) {
        requireState(STARTED);
        stampRecords.addLast(new StampRecord(recordName, stampRecords.getLast()));
    }

    /**
     * 新增一条匿名时间戳
     *
     * @throws IllegalStateException 当前时间线尚未启动或已经结束时时抛出
     */
    public void recordAnonymous() {
        requireState(STARTED);
        stampRecords.addLast(new StampRecord(StampRecord.ANONYMOUS_NAME, stampRecords.getLast()));
    }

    /**
     * 结束当前时间线，时间线结束之后即不可变更，后续的启动或增加记录均无效
     *
     * @throws IllegalStateException 当前时间线尚未启动或已经结束时时抛出
     */
    public void stop() {
        requireState(STARTED);
        stampRecords.addLast(new StampRecord(StampRecord.STOP_NAME, stampRecords.getFirst()));
        state = STOPPED;
    }

    private void requireState(int expectedState) {
        if (state != expectedState) {
            throw new IllegalStateException(name + ": illegal time line state " + state);
        }
    }

    public String getIdentityInfo() {
        return id + "." + name + "." + startThreadName;
    }

    /**
     * 判断此时间线是否匹配给定信息
     *
     * @param timeLineName    时间线名称，null表示不比较
     * @param startThreadName 时间线开启线程名称，null表示不比较
     * @return 此时间线是否匹配给定信息
     */
    public boolean match(String timeLineName, String startThreadName) {
        return (timeLineName == null || name.equals(timeLineName)) &&
            (startThreadName == null || this.startThreadName.equals(startThreadName));
    }
}

