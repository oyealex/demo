/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Client
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/10
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Client().start();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ClientRandomHandler());
                    }
                }).connect("common", 8888).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().addListener(f -> log.info("Client Group Shutdown."));
        }
    }

    @ChannelHandler.Sharable
    private static class ClientRandomHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello", StandardCharsets.UTF_8));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            log.info("[R]: {}", ((ByteBuf) msg).toString(StandardCharsets.UTF_8));
            ctx.close();
        }
    }
}
