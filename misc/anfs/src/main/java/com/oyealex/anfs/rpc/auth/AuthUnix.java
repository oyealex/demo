/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.auth;

import com.oyealex.anfs.rpc.utils.XdrUtil;
import io.netty.buffer.ByteBuf;

import static com.oyealex.anfs.rpc.utils.RpcConstants.AUTH_FLAVOR_SYS;

/**
 * CredentialSystem
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class AuthUnix extends Auth {
    private final String machineName;

    private final int uid;

    private final int gid;

    private final int[] gids;

    public AuthUnix(String machineName, int uid, int gid, int... gids) {
        super(AUTH_FLAVOR_SYS);
        this.machineName = machineName;
        this.uid = uid;
        this.gid = gid;
        this.gids = gids;
    }

    public AuthUnix(String machineName) {
        this(machineName, 0, 0, 0);
    }

    @Override
    public int writeBody(ByteBuf buf) {
        int totalWrittenBytes = 0;
        buf.writeInt((int) (System.currentTimeMillis() / 1000));
        totalWrittenBytes += XdrUtil.writeString(machineName, buf);
        buf.writeInt(uid);
        buf.writeInt(gid);
        totalWrittenBytes += XdrUtil.writeIntArray(gids, buf);
        totalWrittenBytes += 12;
        return totalWrittenBytes;
    }
}
