/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event.annotation;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SerialEventListener
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Order
@EventListener
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerialEventListener {
    @AliasFor(annotation = Order.class, attribute = "value")
    int value() default 0;
}
