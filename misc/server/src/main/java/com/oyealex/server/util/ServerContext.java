/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * TODO 2020/8/30 The ApplicationContext
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Component
public class ServerContext implements ApplicationContextAware {
    @Getter
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }
}
