/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test.async;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.Getter;

import java.util.function.BiConsumer;

/**
 * CallbackPromise
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/23
 */
@Getter
public class CallbackPromise extends DefaultPromise<String> implements GenericFutureListener<Future<String>> {
    private final int xid;

    private final BiConsumer<String, Throwable> callback;

    private final Promise<Void> callbackFinishPromise;

    public CallbackPromise(EventExecutor executor, int xid, BiConsumer<String, Throwable> callback) {
        super(executor);
        this.xid = xid;
        this.callback = callback;
        this.callbackFinishPromise = new DefaultPromise<>(executor);
        addListener(this);
    }

    @Override
    public void operationComplete(Future<String> future) {
        try {
            if (future.isSuccess()) {
                callback.accept(future.getNow(), null);
            } else {
                callback.accept(null, future.cause());
            }
        } finally {
            callbackFinishPromise.setSuccess(null);
        }
    }
}
