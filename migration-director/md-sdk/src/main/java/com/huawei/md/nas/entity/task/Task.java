/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * 任务对象，是迁移工具的顶级对象
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@Getter
@Setter
@NotNull
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Task {
    /** 任务ID */
    @EqualsAndHashCode.Include
    private final String id;

    /** 任务状态 */
    private TaskState state = TaskState.STANDBY;

    /** 运行配置 */
    private final ExecuteConfig executeConfig;
}
