/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.mq.client.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * MgmtMsgPublisher
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
// @Component
@RequiredArgsConstructor
public class MgmtMsgMulticaster {
    @Value("${user.mq.activemq.client.topic.mgmt}")
    private String mgmtTopic;

    private final JmsTemplate template;

    public void multicast(Object msg) {
        template.convertAndSend(mgmtTopic, msg);
    }
}
