package com.yang.chatdemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .remoteAddress("127.0.0.1", 888)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder())
                                .addLast(new StringDecoder())
                                .addLast(new SimpleChannelInboundHandler() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("server say:" + msg);
                                    }
                                });
                    }
                });
        Channel channel = b.connect().channel();
        while (true) {
            Scanner sc = new Scanner(System.in);
            channel.writeAndFlush(sc.nextLine());
        }
    }
}
