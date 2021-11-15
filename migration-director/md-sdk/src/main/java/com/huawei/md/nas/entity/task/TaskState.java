/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务状态
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@RequiredArgsConstructor
public enum TaskState {
    /** 已创建，等待运行 */
    STANDBY(0),
    /** 运行中 */
    RUNNING(1),
    /** 停止中，任务确定停止，正在执行停止的收尾动作 */
    STOPPING(2),
    /** 已停止 */
    STOPPED(3),
    /** 完成中，任务确定完成，正在执行收尾动作 */
    COMPLETING(4),
    /** 已完成 */
    COMPLETED(5),
    /** 已配置定时任务并启动，但是尚未达到执行时刻 */
    SCHEDULED(6),
    ;

    /** 编码到枚举对象的映射 */
    private static final Map<Integer, TaskState> CODE_MAP =
        Arrays.stream(values()).collect(Collectors.toMap(state -> state.code, Function.identity()));

    /** 唯一编码 */
    public final int code;

    @Override
    public String toString() {
        return code + ":" + name();
    }

    /**
     * 从唯一编码解析状态，非标准编码返回{@link #STANDBY}
     *
     * @param code 唯一编码
     * @return 状态
     */
    @NotNull
    @Contract(pure = true)
    public static TaskState parse(int code) {
        return CODE_MAP.getOrDefault(code, STANDBY);
    }
}
