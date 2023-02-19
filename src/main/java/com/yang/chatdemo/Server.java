package com.yang.chatdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Map<SocketAddress, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(888)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        SocketAddress address = ctx.channel().remoteAddress();
                                        System.out.println("客户端" + address + "已连接");
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("server accept:" + msg);
                                        ctx.writeAndFlush(Unpooled.copiedBuffer("id accept:" + msg, CharsetUtil.UTF_8));
                                    }
                                });
                    }
                });
        ChannelFuture f = b.bind().sync();
        f.channel().closeFuture().sync();
    }
}
