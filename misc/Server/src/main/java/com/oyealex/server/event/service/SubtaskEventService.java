/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event.service;

import com.oyealex.server.event.FailedEvent;
import com.oyealex.server.event.NfsSubtask;
import com.oyealex.server.event.Subtask;
import com.oyealex.server.event.annotation.SerialEventListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * SubtaskEventService
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Slf4j
@Component
public class SubtaskEventService implements Serializable {
    @SerialEventListener(100)
    public void onSubtaskStart(@NotNull Subtask subtask) {
        log.info("subtask {} start", subtask);
    }

    @SerialEventListener(200)
    public void onSubtaskStartException(@NotNull Subtask subtask) {
        log.info("subtask {} is going to failed", subtask);
        throw new RuntimeException(subtask.getId());
    }

    @SerialEventListener
    public void onSubtaskStartFailed(@NotNull FailedEvent<NfsSubtask> failedEvent) {
        log.info("nfs subtask {} start failed: ", failedEvent.getData(), failedEvent.getCause());
    }
}
