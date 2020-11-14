/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.rest.uri.auto.scan;

import com.oyealex.server.rest.uri.UriManager;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * TODO 2020/8/31 The CachedRestResponseTypeAfterApplicationRegister
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/31
 */
@Component
public class CachedRestResponseTypeAfterContextRefreshedRegister implements ApplicationListener<ContextRefreshedEvent> {
    @Setter(onMethod_ = {@Autowired})
    private RestResponseTypeCache restResponseTypeCache;

    @Setter(onMethod_ = {@Autowired})
    private UriManager uriManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        restResponseTypeCache.registerCacheTo(uriManager);
    }
}
