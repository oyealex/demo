/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import static com.oyealex.anfs.rpc.utils.RpcConstants.ACCEPT_STAT_SUCCESS;
import static com.oyealex.anfs.rpc.utils.RpcConstants.REPLY_STAT_ACCEPTED;

/**
 * RpcResponse
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
 * union reply_body switch (reply_stat stat) {
 *     case MSG_ACCEPTED:
 *         accepted_reply areply;
 *     case MSG_DENIED:
 *         rejected_reply rreply;
 * } reply;
 *
 * struct accepted_reply {
 *     opaque_auth verf;
 *     union switch (accept_stat stat) {
 *         case SUCCESS:
 *             opaque results[0];
 *             // procedure-specific results start here
 *         case PROG_MISMATCH:
 *             struct {
 *                 unsigned int low;
 *                 unsigned int high;
 *             } mismatch_info;
 *         default:
 *             // Void. Cases include PROG_UNAVAIL, PROC_UNAVAIL, GARBAGE_ARGS, and SYSTEM_ERR.
 *             void;
 *     } reply_data;
 * };
 *
 * union rejected_reply switch (reject_stat stat) {
 *     case RPC_MISMATCH:
 *         struct {
 *             unsigned int low;
 *             unsigned int high;
 *         } mismatch_info;
 *     case AUTH_ERROR:
 *         auth_stat stat;
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public abstract class RpcReply {
    @Getter
    private final int xid;

    protected RpcReply(int xid) {
        this.xid = xid;
    }

    public final void read(ByteBuf buf) throws RpcException {
        buf.skipBytes(4); // skip message type, must be reply(1)
        int replyStat = buf.readInt();
        if (replyStat == REPLY_STAT_ACCEPTED) {
            buf.skipBytes(4).skipBytes(buf.readInt()); // skip verifier
            int acceptStat = buf.readInt();
            if (acceptStat != ACCEPT_STAT_SUCCESS) {
                throw new RpcException("Rpc Accepted with unsuccessful stat: " + acceptStat);
            }
        } else {
            int rejectStat = buf.readInt();
            throw new RpcException("Rpc Rejected: " + rejectStat);
        }
        readProcedure(buf);
    }

    protected abstract void readProcedure(ByteBuf buf) throws RpcException;
}
