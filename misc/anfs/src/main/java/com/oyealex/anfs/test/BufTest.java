/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test;

import com.oyealex.anfs.portmap.GetPortRequest;
import com.oyealex.anfs.rpc.auth.AuthNone;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_MOUNT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_MOUNT_V3;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROTO_TCP;

/**
 * BufTest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/13
 */
@Slf4j
public class BufTest {
    public static void main(String[] args) {
        GetPortRequest call = new GetPortRequest(
            AuthNone.INSTANCE,
            AuthNone.INSTANCE,
            PROGRAM_MOUNT,
            PROGRAM_VERS_MOUNT_V3,
            PROTO_TCP);
        ByteBuf buf = Unpooled.buffer();
        call.write(buf, true);
        int index = 0;
        while (buf.readableBytes() > 0) {
            index++;
            System.out.printf("%02x ", buf.readByte());
            if (index == 8) {
                System.out.print(" ");
            } else if (index == 16) {
                System.out.println();
                index = 0;
            }
        }
    }
}
