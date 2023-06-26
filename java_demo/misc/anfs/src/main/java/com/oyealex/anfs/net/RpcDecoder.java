/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.net;

import com.oyealex.anfs.rpc.utils.RpcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.LinkedList;
import java.util.List;

/**
 * RpcDecoder
 * <p/>
 * Decode bytes into rpc entity.
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/11
 */
public class RpcDecoder extends ByteToMessageDecoder {
    /** save temp fragments */
    private final LinkedList<ByteBuf> fragments = new LinkedList<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // rpc fragment header is 4 bytes
        while (in.readableBytes() > 4) {
            in.markReaderIndex();

            // parse header
            long fragmentHeader = in.readUnsignedInt();
            long fragmentSize = RpcUtil.getFragmentLength(fragmentHeader);
            if (in.readableBytes() < fragmentSize) {
                // not enough for current fragment
                in.resetReaderIndex();
                return;
            }

            fragments.add(in.readRetainedSlice((int) fragmentSize)); // avoid array copy
            if (RpcUtil.isLastFragment(fragmentHeader)) { // all fragments collected
                if (fragments.size() == 1) {
                    out.add(fragments.remove()); // only one fragment
                } else {
                    // composite all fragments to one rpc entity
                    CompositeByteBuf fragmentsBuf = ctx.alloc().compositeBuffer(fragments.size());
                    fragmentsBuf.addComponents(true, fragments);
                    fragments.clear();
                    out.add(fragmentsBuf);
                }
                return;
            }
        }
    }
}
