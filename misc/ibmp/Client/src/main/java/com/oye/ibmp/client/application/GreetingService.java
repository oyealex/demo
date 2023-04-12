package com.oye.ibmp.client.application;

import com.oye.ibmp.client.domain.config.RedissonCacheable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * CachedMsgService
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Slf4j
@Service
public class GreetingService {
    // @RedissonCacheable(cacheNames = "test", key = "#msg", refreshMethod = "refresh", ttl = 3L, unit = TimeUnit.SECONDS,
    //     maxIdleTime = 3L)
    @RedissonCacheable(cacheNames = "test", key = "#msg", refreshMethod = "refresh")
    public String greet(String msg) {
        log.info("Receive a greeting msg: {}", msg);
        return "Response for greeting msg: " + msg;
    }

    public void refresh() {
        log.info("refresh");
    }
}
