/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc;

import com.oyealex.anfs.rpc.auth.Auth;
import com.oyealex.anfs.rpc.utils.RpcUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static com.oyealex.anfs.rpc.utils.RpcConstants.MSG_TYPE_CALL;

/**
 * RpcRequest
 * <pre><code>
 * struct rpc_msg {
 *     unsigned int xid;
 *     union switch (msg_type mtype) {
 *         case CALL:
 *             call_body cbody;
 *         case REPLY:
 *             reply_body rbody;
 *     } body;
 * };
 *
 * struct call_body {
 *     unsigned int rpcvers; // must be equal to two (2)
 *     unsigned int prog;
 *     unsigned int vers;
 *     unsigned int proc;
 *     opaque_auth cred;
 *     opaque_auth verf;
 *     // procedure specific parameters start here
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public abstract class RpcCall {
    private static final int INIT_XID = (new SecureRandom().nextInt() << 16) |
        (((int) System.currentTimeMillis()) >> 16);

    private static final AtomicInteger XID_GEN = new AtomicInteger(INIT_XID);

    @Getter
    private final int xid = XID_GEN.incrementAndGet();

    private final int program;

    private final int version;

    private final int procedure;

    private final Auth credentials;

    private final Auth verifier;

    protected RpcCall(int program, int version, int procedure, Auth credentials, Auth verifier) {
        this.program = program;
        this.version = version;
        this.procedure = procedure;
        this.credentials = credentials;
        this.verifier = verifier;
    }

    public final void write(ByteBuf buf, boolean isLast) {
        int startIndex = buf.writeInt(0).writerIndex(); // for fragment header, will be written latter
        buf.writeInt(xid)
            .writeInt(MSG_TYPE_CALL)
            .writeInt(2) // rpc version: must be 2
            .writeInt(program)
            .writeInt(version)
            .writeInt(procedure);
        credentials.write(buf);
        verifier.write(buf);
        writeProcedure(buf);
        int totalSize = buf.writerIndex() - startIndex;

        buf.writerIndex(startIndex - 4)
            .writeInt((isLast ? RpcUtil.LAST_FRAGMENT_MASK : 0) | totalSize)
            .writerIndex(startIndex + totalSize);
    }

    protected abstract void writeProcedure(ByteBuf buf);
}
