package com.oyealex.server.controller;

import com.oyealex.server.event.CifsSubtask;
import com.oyealex.server.event.NfsSubtask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author oye
 * @since 2020-05-14 23:43:49
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonController {
    private final ApplicationEventPublisher publisher;

    @PostMapping("/task/start/{id}")
    public void startTask(@PathVariable("id") @NotNull String taskId) {
        publisher.publishEvent(taskId.startsWith("N") ? new NfsSubtask(taskId) : new CifsSubtask(taskId));
    }
}
