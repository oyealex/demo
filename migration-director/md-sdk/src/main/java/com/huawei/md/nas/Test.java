/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas;

import com.google.gson.Gson;
import com.huawei.md.nas.entity.task.ScheduleConfig;

/**
 * Test
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-10
 */
public class Test {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String now = gson.toJson(ScheduleConfig.UNSET);
        System.out.println(now);
        System.out.println(gson.fromJson(now, ScheduleConfig.class));
    }
}
