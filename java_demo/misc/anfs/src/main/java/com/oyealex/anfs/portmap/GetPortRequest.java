/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.portmap;

import com.oyealex.anfs.rpc.RpcCall;
import com.oyealex.anfs.rpc.auth.Auth;
import com.oyealex.anfs.rpc.auth.AuthNone;
import io.netty.buffer.ByteBuf;

import static com.oyealex.anfs.rpc.utils.RpcConstants.PROCEDURE_PORTMAP_GETPORT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_PORTMAP;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_PORTMAP_V2;

/**
 * GetPortRequest
 * <pre><code>
 * struct mapping {
 *     unsigned int prog;
 *     unsigned int vers;
 *     unsigned int prot;
 *     unsigned int port;
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class GetPortRequest extends RpcCall {
    private final int queryProgram;

    private final int programVer;

    private final int protocol;

    public GetPortRequest(int queryProgram, int programVer, int protocol) {
        this(AuthNone.INSTANCE, AuthNone.INSTANCE, queryProgram, programVer, protocol);
    }

    public GetPortRequest(Auth credentials, Auth verifier, int queryProgram, int programVer, int protocol) {
        super(PROGRAM_PORTMAP, PROGRAM_VERS_PORTMAP_V2, PROCEDURE_PORTMAP_GETPORT, credentials, verifier);
        this.queryProgram = queryProgram;
        this.programVer = programVer;
        this.protocol = protocol;
    }

    @Override
    protected void writeProcedure(ByteBuf buf) {
        buf.writeInt(queryProgram)
            .writeInt(programVer)
            .writeInt(protocol)
            .writeInt(0); // port param ignored by get port
    }
}
