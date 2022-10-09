/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test.async;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * CallbackExecPromise
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/23
 */
public class CallbackExecPromise extends DefaultPromise<Void> implements GenericFutureListener<Future<String>> {
    public CallbackExecPromise(EventExecutor executor) {
        super(executor);
    }

    @Override
    public void operationComplete(Future<String> future) {

    }
}
