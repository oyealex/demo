/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.mount;

import com.oyealex.anfs.rpc.RpcException;

/**
 * MountException
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class MountException extends RpcException {
    public MountException(String message) {
        super(message);
    }

    public MountException(String message, Throwable cause) {
        super(message, cause);
    }
}
