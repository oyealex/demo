/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SelfEvent
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-15
 */
@Order
@EventListener
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfEvent {
    @AliasFor(annotation = Order.class, attribute = "value")
    int value() default Ordered.LOWEST_PRECEDENCE;

    @AliasFor(annotation = EventListener.class, attribute = "classes")
    Class<?>[] classes() default {};
}
