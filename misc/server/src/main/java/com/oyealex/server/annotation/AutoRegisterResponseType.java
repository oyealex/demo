/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 2020/8/30 The Target
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoRegisterResponseType {
}
