/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.net;

import com.oyealex.anfs.rpc.RpcCall;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RpcEncoder
 * <p/>
 * Encode rpc entity into bytes.
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/11
 */
public class RpcEncoder extends MessageToByteEncoder<RpcCall> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcCall msg, ByteBuf out) {
        msg.write(out, true);
    }
}
