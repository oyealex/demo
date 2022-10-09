/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc;

/**
 * RpcException
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class RpcException extends Exception {
    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
