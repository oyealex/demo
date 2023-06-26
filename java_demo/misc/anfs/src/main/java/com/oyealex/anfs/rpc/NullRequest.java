/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc;

import com.oyealex.anfs.rpc.auth.Auth;
import io.netty.buffer.ByteBuf;

/**
 * NullRequest
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class NullRequest extends RpcCall {
    public NullRequest(int program, int version, int procedure, Auth credentials,
        Auth verifier) {
        super(program, version, procedure, credentials, verifier);
    }

    @Override
    protected void writeProcedure(ByteBuf buf) {}
}
