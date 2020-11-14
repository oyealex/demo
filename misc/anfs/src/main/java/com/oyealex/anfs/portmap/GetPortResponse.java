/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.portmap;

import com.oyealex.anfs.rpc.RpcReply;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * GetPortResponse
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class GetPortResponse extends RpcReply {
    @Getter
    private int port;

    public GetPortResponse(int xid) {
        super(xid);
    }

    @Override
    protected void readProcedure(ByteBuf buf) {
        port = buf.readInt();
    }
}
