/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Server
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/10
 */
@Slf4j
@RequiredArgsConstructor
public class Server {
    public static void main(String[] args) throws InterruptedException {
        new Server(8888).start();
    }

    private final int port;

    public void start() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ServerEchoHandler());
                    }
                });
            bootstrap.bind().sync().channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully().addListener(f -> log.info("Parent Group Shutdown."));
            childGroup.shutdownGracefully().addListener(f -> log.info("Child Group Shutdown."));
        }
    }

    private static class ServerEchoHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            String cmd = buf.toString(StandardCharsets.UTF_8);
            buf.release();

            log.info("<<: {}", cmd);
            ctx.channel().eventLoop().execute(() -> {

            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("ACK", StandardCharsets.UTF_8));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Ready: {}", ctx.channel().remoteAddress());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.info("Inactive: {}", ctx.channel().remoteAddress());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("[Server Exception]: ", cause);
            ctx.close();
        }
    }
}
