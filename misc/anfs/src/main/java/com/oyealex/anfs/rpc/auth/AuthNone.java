/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.auth;

import io.netty.buffer.ByteBuf;

import static com.oyealex.anfs.rpc.utils.RpcConstants.AUTH_FLAVOR_NONE;

/**
 * AuthNone
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class AuthNone extends Auth {
    public static final Auth INSTANCE = new AuthNone();

    private AuthNone() {
        super(AUTH_FLAVOR_NONE);
    }

    @Override
    public int writeBody(ByteBuf buf) {
        return 0;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(flavor);
        buf.writeInt(0);
    }
}
