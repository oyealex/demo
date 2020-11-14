/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test;

import com.oyealex.anfs.mount.MntRequest;
import com.oyealex.anfs.mount.MntResponse;
import com.oyealex.anfs.net.Connection;
import com.oyealex.anfs.portmap.GetPortRequest;
import com.oyealex.anfs.portmap.GetPortResponse;
import com.oyealex.anfs.rpc.NullRequest;
import com.oyealex.anfs.rpc.NullResponse;
import com.oyealex.anfs.rpc.auth.AuthNone;
import com.oyealex.anfs.rpc.auth.AuthUnix;
import com.oyealex.anfs.rpc.utils.XdrUtil;
import io.netty.channel.nio.NioEventLoopGroup;

import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_MOUNT;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_NFS;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_MOUNT_V3;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROGRAM_VERS_NFS_V3;
import static com.oyealex.anfs.rpc.utils.RpcConstants.PROTO_TCP;

/**
 * Main
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // String host = "47.106.186.71";
        String host = "common";
        // String export = "/mnt/nfs";
        String export = "/tmp123123";

        NioEventLoopGroup group = new NioEventLoopGroup();
        Connection portmapConn = new Connection(host, 111, group).connect();
        portmapConn
            .sendAsync(new GetPortRequest(PROGRAM_NFS, PROGRAM_VERS_NFS_V3, PROTO_TCP), GetPortResponse::new,
                future -> System.out.println("nfs port: " + ((GetPortResponse) future.getNow()).getPort()))
            .sendAsync(new GetPortRequest(PROGRAM_MOUNT, PROGRAM_VERS_MOUNT_V3, PROTO_TCP), GetPortResponse::new,
                future -> {
                    System.out.println("port port: " + ((GetPortResponse) future.getNow()).getPort());
                    portmapConn.disconnect();
                });

        Connection mountConn = new Connection(host, 20048, group).connect();
        AuthUnix auth = new AuthUnix("common", 0, 0, 0);
        mountConn.sendAsync(new NullRequest(
                PROGRAM_MOUNT,
                PROGRAM_VERS_MOUNT_V3,
                0,
                AuthNone.INSTANCE,
                AuthNone.INSTANCE),
            NullResponse::new, future -> System.out.println("mount null: " + future.isSuccess()))
            .sendAsync(new MntRequest(auth, AuthNone.INSTANCE, export), MntResponse::new,
                future -> {
                    if (future.isSuccess()) {
                        MntResponse mntResponse = (MntResponse) future.getNow();
                        System.out.println(XdrUtil.dumpToHexString(mntResponse.getFileHandle()));
                    } else {
                        System.out.println("mount failed");
                    }
                    mountConn.disconnect();
                });
    }
}
