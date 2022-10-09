/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test.async;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Connection
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/23
 */
public class Connection {
    private static final AtomicInteger XID = new AtomicInteger(0);

    private final String host;

    private final int port;

    private final EventLoopGroup ioGroup;

    private final EventLoopGroup bzGroup;

    private final Map<Integer, Promise<?>> callbackMap = new ConcurrentHashMap<>();

    private Channel channel;

    public Connection(String host, int port, int ioThreads, int bzThreads) {
        this.host = host;
        this.port = port;
        this.ioGroup = new NioEventLoopGroup(ioThreads, new DefaultThreadFactory("io"));
        this.bzGroup = new DefaultEventLoopGroup(bzThreads, new DefaultThreadFactory("bz"));
    }

    public void connect() throws InterruptedException {
        channel = new Bootstrap().group(ioGroup).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast();
            }
        }).connect(host, port).sync().channel();
    }
}
