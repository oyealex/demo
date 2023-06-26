/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.mount;

import com.oyealex.anfs.rpc.RpcCall;
import com.oyealex.anfs.rpc.auth.Auth;
import com.oyealex.anfs.rpc.utils.XdrUtil;
import io.netty.buffer.ByteBuf;

import static com.oyealex.anfs.rpc.utils.RpcConstants.PROCEDURE_MOUNT_MNT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_MOUNT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_MOUNT_V3;

/**
 * MntRequest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class MntRequest extends RpcCall {
    private final String export;

    public MntRequest(Auth credentials, Auth verifier, String export) {
        super(PROGRAM_MOUNT, PROGRAM_VERS_MOUNT_V3, PROCEDURE_MOUNT_MNT, credentials, verifier);
        this.export = export;
    }

    @Override
    protected void writeProcedure(ByteBuf buf) {
        XdrUtil.writeString(export, buf);
    }
}
