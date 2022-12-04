package com.oyealex.exp.annotationex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OptionalGetter
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface OptionalGetter {
}
