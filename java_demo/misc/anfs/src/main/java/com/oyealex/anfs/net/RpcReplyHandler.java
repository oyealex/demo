/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * RpcReplyHandler
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/13
 */
@Slf4j
public class RpcReplyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf rpcBuf = (ByteBuf) msg;
        try {
            parseRpcResponse(ctx.channel().attr(Connection.RPC_CONNECTION_ATTR_KEY).get(),
                rpcBuf);
        } finally {
            rpcBuf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception on: {}", ctx.channel(), cause);
        super.exceptionCaught(ctx, cause);
    }

    private void parseRpcResponse(Connection connection, ByteBuf rpcBuf) {
        int xid = rpcBuf.readInt();
        connection.notifyReply(xid, rpcBuf);
    }
}
