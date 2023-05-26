package com.oyealex.pipe.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * 标记相比原生Stream API扩展的方法
 *
 * @author oyealex
 * @since 2023-03-05
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Todo {
}
