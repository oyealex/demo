/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event.annotation;

import com.oyealex.server.event.FailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * SerialEventListenerAspect
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SerialEventListenerAspect {
    private final ApplicationEventPublisher publisher;

    @Pointcut("@annotation(com.oyealex.server.event.annotation.SerialEventListener)")
    public void cut() {}

    @Around("cut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable cause) {
            publisher.publishEvent(new FailedEvent<>(joinPoint.getArgs()[0], cause));
            return null;
        }
    }
}
