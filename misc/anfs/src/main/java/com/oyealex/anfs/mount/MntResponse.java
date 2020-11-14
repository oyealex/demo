/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.mount;

import com.oyealex.anfs.rpc.RpcException;
import com.oyealex.anfs.rpc.RpcReply;
import com.oyealex.anfs.rpc.utils.RpcConstants;
import com.oyealex.anfs.rpc.utils.XdrUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * MntResponse
 * <pre><code>
 * mountres3 MOUNTPROC3_MNT(dirpath) = 1;
 *
 * struct mountres3_ok {
 *     fhandle3 fhandle;
 *     int auth_flavors<>;
 * };
 *
 * union mountres3 switch (mountstat3 fhs_status) {
 *     case MNT_OK:
 *         mountres3_ok mountinfo;
 *     default:
 *         void;
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class MntResponse extends RpcReply {
    @Getter
    private byte[] fileHandle;

    @Getter
    private int[] flavors;

    public MntResponse(int xid) {
        super(xid);
    }

    @Override
    protected void readProcedure(ByteBuf buf) throws RpcException {
        int mountStat = buf.readInt();
        if (mountStat != RpcConstants.MNT3_OK) {
            throw new MountException("Mount failed: " + mountStat);
        }
        fileHandle = XdrUtil.readByteArray(buf);
        flavors = XdrUtil.readIntArray(buf);
    }
}
