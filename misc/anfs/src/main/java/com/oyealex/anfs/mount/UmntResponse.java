/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.mount;

import com.oyealex.anfs.rpc.RpcReply;
import io.netty.buffer.ByteBuf;

/**
 * UmntResponse
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class UmntResponse extends RpcReply {
    public UmntResponse(int xid) {
        super(xid);
    }

    @Override
    protected void readProcedure(ByteBuf buf) {}
}
