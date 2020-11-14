/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * EventTest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-06
 */
@Slf4j
@Component
public class EventTestService {
    @SneakyThrows
    @Async
    @Order(0)
    @EventListener
    public void onTaskStart(@NotNull String taskId) {
        log.info("task {} start.start", taskId);
        TimeUnit.SECONDS.sleep(2L);
        log.info("task {} start.end", taskId);
    }

    @Async
    @Order(1000)
    @EventListener
    public void initTaskRunEnv(@NotNull String taskId) throws InterruptedException {
        log.info("init run environment for task {}.start", taskId);
        TimeUnit.SECONDS.sleep(2L);
        log.info("init run environment for task {}.end", taskId);
    }
}
