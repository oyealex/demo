/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.mount;

import com.oyealex.anfs.rpc.RpcCall;
import com.oyealex.anfs.rpc.auth.Auth;
import io.netty.buffer.ByteBuf;

import static com.oyealex.anfs.rpc.utils.RpcConstants.PROCEDURE_MOUNT_EXPORT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_MOUNT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_MOUNT_V3;

/**
 * ExportRequest
 * <pre><code>
 * exports MOUNTPROC3_EXPORT(void) = 5;
 *
 * typedef struct groupnode *groups;
 *
 * struct groupnode {
 *     name gr_name;
 *     groups gr_next;
 * };
 *
 * typedef struct exportnode *exports;
 *
 * struct exportnode {
 *     dirpath ex_dir;
 *     groups ex_groups;
 *     exports ex_next;
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class ExportRequest extends RpcCall {
    public ExportRequest(Auth credentials, Auth verifier) {
        super(PROGRAM_MOUNT, PROGRAM_VERS_MOUNT_V3, PROCEDURE_MOUNT_EXPORT, credentials, verifier);
    }

    @Override
    protected void writeProcedure(ByteBuf buf) {

    }
}
