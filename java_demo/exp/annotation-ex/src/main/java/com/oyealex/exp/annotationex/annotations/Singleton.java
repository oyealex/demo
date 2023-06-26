package com.oyealex.exp.annotationex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单例注解
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Singleton {
}
