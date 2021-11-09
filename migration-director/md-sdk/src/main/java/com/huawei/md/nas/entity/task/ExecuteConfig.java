/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import com.huawei.md.nas.entity.task.path.MigrationPath;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * 任务运行配置，包含静态的运行配置
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@NotNull
@ToString
@RequiredArgsConstructor
public class ExecuteConfig {
    /** 源端迁移路径 */
    private final MigrationPath sourcePath;

    /** 目的端迁移路径 */
    private final MigrationPath targetPath;

    /** 任务定时执行配置 */
    @Setter
    private ScheduleConfig scheduleConfig = ScheduleConfig.UNSET;
}
