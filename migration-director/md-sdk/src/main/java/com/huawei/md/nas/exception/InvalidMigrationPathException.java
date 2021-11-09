/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.exception;

/**
 * InvalidMigrationPathException
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
public class InvalidMigrationPathException extends MDRuntimeException {
    public InvalidMigrationPathException() {
    }

    public InvalidMigrationPathException(String message) {
        super(message);
    }

    public InvalidMigrationPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMigrationPathException(Throwable cause) {
        super(cause);
    }
}
