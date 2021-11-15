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
 * TaskResult
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-10
 */
@RequiredArgsConstructor
public enum TaskResult {
    SUCCESSFUL(0),
    PARTIAL_SUCCESSFUL(1),
    FAILED(2),
    ;

    /** 编码到枚举对象的映射 */
    private static final Map<Integer, TaskResult> CODE_MAP =
        Arrays.stream(values()).collect(Collectors.toMap(result -> result.code, Function.identity()));

    /** 唯一编码 */
    public final int code;

    @Override
    public String toString() {
        return code + ":" + name();
    }

    /**
     * 从唯一编码解析结果，非标准编码返回{@link #FAILED}
     *
     * @param code 唯一编码
     * @return 结果
     */
    @NotNull
    @Contract(pure = true)
    public static TaskResult parse(int code) {
        return CODE_MAP.getOrDefault(code, FAILED);
    }
}
