/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.mq.broker;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * BrokerService
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Component
public class EmbedBrokerService implements ApplicationRunner {
    private final BrokerService broker = new BrokerService();

    @Value("${user.mq.activemq.broker.id}")
    private String brokerId;

    @Value("${user.mq.activemq.broker.connector}")
    private String connector;

    @Override
    public void run(ApplicationArguments ignored) throws Exception {
        broker.setBrokerId(brokerId);
        broker.addConnector(connector);
        broker.setPersistenceAdapter(new KahaDBStore());
        broker.start();
    }
}
