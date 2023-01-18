package com.oye.ibmp.client.infrastructure.runner;

import com.oye.ibmp.client.application.GreetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * CacheTestRunner
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheTestRunner implements ApplicationRunner {
    private final CacheManager cacheManager;

    private final GreetingService greetingService;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        log.info("greeting: {}", greetingService.greet("jack"));
        log.info("greeting: {}", greetingService.greet("jack"));
        Thread.sleep(500L);
        log.info("greeting: {}", greetingService.greet("jack"));
        Thread.sleep(500L);
        log.info("greeting: {}", greetingService.greet("jack"));
        Thread.sleep(500L);
        log.info("greeting: {}", greetingService.greet("jack"));
        Thread.sleep(1500L);
        log.info("greeting: {}", greetingService.greet("jack"));
        log.info("greeting: {}", greetingService.greet("jack"));
    }
}
