/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 任务定时执行配置
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-10
 */
@Getter
@ToString
@Immutable
@RequiredArgsConstructor
public class ScheduleConfig {
    /** 未配置定时执行的默认值 */
    public static final ScheduleConfig UNSET;

    static {
        LocalDateTime localZeroDateTime = LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC);
        ZonedDateTime zonedZeroDateTime = ZonedDateTime.of(localZeroDateTime, ZoneId.systemDefault());
        UNSET = new ScheduleConfig(zonedZeroDateTime, zonedZeroDateTime);
    }

    /** 定时开始时间 */
    private final ZonedDateTime startAt;

    /** 定时停止时间 */
    private final ZonedDateTime stopAt;
}
