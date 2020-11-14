/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time.recorder;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间线记录器<p/>
 * 通过开启{@link TimeLine}时间线并添加{@link StampRecord}时间戳记录来记载线程内各个阶段的时间戳和相邻时间戳之间的耗时。<br/>
 * 通过{@link ThreadLocal}实现线程安全，每个线程独立记录，并通过{@link TimeLineRecorder#formatStatistics()}
 * 提供统计和可读格式化功能。<br/>
 * <p/>
 * TodoList: 2021/1/16<br/>
 * 1. 提供同名命名时间戳关联计算耗时功能<br/>
 * 2. 提供线程切换跟踪功能<br/>
 * 3. 提供通过注解自动记录功能<br/>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/1/15
 */
@SuppressWarnings("unused")
public class TimeLineRecorder {
    private static final TimeLineRecorder GLOBAL_RECORDER = new TimeLineRecorder();

    /** 当前记录器所有的时间线 */
    @Getter(AccessLevel.PACKAGE)
    private final Set<TimeLine> timeLines = ConcurrentHashMap.newKeySet();

    /** 当前线程的时间线 */
    private final ThreadLocal<TimeLine> currentTimeLineThreadLocal = new ThreadLocal<>();

    /**
     * 获取全局时间记录器
     *
     * @return 全局时间记录器
     */
    public static TimeLineRecorder global() {
        return GLOBAL_RECORDER;
    }

    /**
     * 以给定名称在当前线程开启一条新的时间线，并替换为当前时间线<p/>
     * 时间线会自动记录开启时所在线程名称；<br/>
     * 每个已存在的时间线仅能开启一次，然后通过{@link #record(String)}记录命名时间戳，
     * 每条记录的耗时为该记录到上一条记录所经过的时间；<br/>
     * 通过{@link #recordAnonymous()}记录匿名时间戳，匿名时间戳不参与统计计算，仅用于辅佐下一次命名时间戳计算耗时；<br/>
     * 时间线通过{@link #stop()}关闭，关闭后时间线信息将无法更改。
     *
     * @param timeLineName 时间线名称
     */
    public void start(String timeLineName) {
        final TimeLine timeLine = TimeLine.createStarted(timeLineName);
        currentTimeLineThreadLocal.set(timeLine);
        timeLines.add(timeLine);
    }

    /**
     * 在当前时间线上以给定名称新增一条时间戳记录
     *
     * @param recordName 新的时间戳记录名称
     * @throws IllegalStateException 当前时间线不存在、尚未启动或已经结束时抛出
     */
    public void record(String recordName) {
        getCurrentTimeLine().record(recordName);
    }

    /**
     * 在当前时间线上新增一条匿名时间戳记录
     *
     * @throws IllegalStateException 当前时间线不存在、尚未启动或已经结束时抛出
     */
    public void recordAnonymous() {
        getCurrentTimeLine().recordAnonymous();
    }

    /**
     * 停止当前时间线
     *
     * @throws IllegalStateException 当前时间线不存在、尚未启动或已经结束时抛出
     */
    public void stop() {
        getCurrentTimeLine().stop();
        currentTimeLineThreadLocal.remove();
    }

    /**
     * 清空当前保存的所有时间线记录
     */
    public void clear() {
        timeLines.clear();
    }

    /**
     * 获取当前所有保存的时间线数量
     *
     * @return 当前所有保存的时间线数量
     */
    public int size() {
        return timeLines.size();
    }

    /**
     * 对当前所有的时间线记录执行统计，并格式化为可读形式
     *
     * @return 所有时间线记录可读形式的统计结果
     */
    public String formatStatistics() {
        return Formatter.formatStatistics(this, null, null);
    }

    /**
     * 按照给定条件过滤匹配的时间线记录，执行统计并格式化为可读形式
     *
     * @param timeLineName 时间线名称，null表示不过滤
     * @param startThread  时间线开始线程名称，null表示不过滤
     * @return 可读形式的统计结果
     */
    public String formatStatistics(String timeLineName, String startThread) {
        return Formatter.formatStatistics(this, timeLineName, startThread);
    }

    /**
     * 按照给定条件过滤匹配的时间线，分别格式化为可读形式
     *
     * @param timeLineName 时间线名称，null表示不过滤
     * @param startThread  时间线开始线程名称，null表示不过滤
     * @return 匹配的时间线对应的可读字符串
     */
    public List<String> formatTimeLines(String timeLineName, String startThread) {
        return Formatter.formatTimeLines(this, timeLineName, startThread);
    }

    private TimeLine getCurrentTimeLine() {
        TimeLine timeLine;
        if ((timeLine = currentTimeLineThreadLocal.get()) == null) {
            throw new IllegalStateException("no timeline exist");
        }
        return timeLine;
    }
}
