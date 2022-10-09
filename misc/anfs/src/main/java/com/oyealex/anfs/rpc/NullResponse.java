/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc;

import io.netty.buffer.ByteBuf;

/**
 * NullResponse
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class NullResponse extends RpcReply {
    public NullResponse(int xid) {
        super(xid);
    }

    @Override
    protected void readProcedure(ByteBuf buf) {}
}
