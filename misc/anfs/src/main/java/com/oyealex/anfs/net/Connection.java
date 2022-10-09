/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.net;

import com.oyealex.anfs.rpc.RpcCall;
import com.oyealex.anfs.rpc.RpcException;
import com.oyealex.anfs.rpc.RpcReply;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * RpcConnection
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/11
 */
@Slf4j
@RequiredArgsConstructor
public class Connection {
    public static final AttributeKey<Connection> RPC_CONNECTION_ATTR_KEY = AttributeKey.valueOf("__RPC_CONN__");

    private final String host;

    private final int port;

    private final EventLoopGroup loopGroup;

    private final Map<Integer, Promise<RpcReply>> promises = new ConcurrentHashMap<>();

    private final Map<Integer, IntFunction<RpcReply>> replyConstructors = new ConcurrentHashMap<>();

    private Channel channel;

    public Connection connect() throws InterruptedException {
        channel = new Bootstrap()
            .group(loopGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
            .attr(RPC_CONNECTION_ATTR_KEY, this)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addFirst(new RpcEncoder());
                    ch.pipeline().addLast(new RpcDecoder(), new RpcReplyHandler());
                }
            }).connect(host, port).sync().channel();
        return this;
    }

    public void disconnect() {
        channel.disconnect();
    }

    public Connection sendAsync(RpcCall call, IntFunction<RpcReply> replyConstruct,
        Consumer<Future<? super RpcReply>> callback) throws InterruptedException {
        DefaultPromise<RpcReply> promise = new DefaultPromise<>(loopGroup.next());
        promise.addListener(callback::accept);
        replyConstructors.put(call.getXid(), replyConstruct);
        promises.put(call.getXid(), promise);
        channel.writeAndFlush(call).sync();
        return this;
    }

    void notifyReply(int xid, ByteBuf rpcBuf) {
        IntFunction<RpcReply> replyConstructor = replyConstructors.remove(xid);
        Promise<RpcReply> promise = promises.remove(xid);
        if (replyConstructor == null) {
            promise.setFailure(new NullPointerException("no reply constructor found for: " + xid));
            return;
        }
        RpcReply reply = replyConstructor.apply(xid);
        try {
            reply.read(rpcBuf);
            promise.setSuccess(reply);
        } catch (RpcException exception) {
            promise.setFailure(exception);
        }
    }
}
