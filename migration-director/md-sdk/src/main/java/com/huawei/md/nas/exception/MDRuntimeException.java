/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.exception;

/**
 * MDRuntimeException
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
public class MDRuntimeException extends RuntimeException {
    public MDRuntimeException() {
        super();
    }

    public MDRuntimeException(String message) {
        super(message);
    }

    public MDRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MDRuntimeException(Throwable cause) {
        super(cause);
    }
}
