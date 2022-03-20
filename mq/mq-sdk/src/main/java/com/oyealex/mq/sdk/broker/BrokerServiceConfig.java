/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.mq.sdk.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * BrokerServiceConfig
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-25
 */
@Configuration
public class BrokerServiceConfig {
    @Value("${activemq.broker.url:tcp:/localhost:61616}")
    private String brokerUrl;


}
