package com.oyealex.pipe.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * 标记原生Stream API的方法
 *
 * @author oyealex
 * @since 2023-03-05
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Classical {
}
