package com.yang.simpledemo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
    private final String host;
    private final int prot;

    public EchoClient(String host, int prot) {
        this.host = host;
        this.prot = prot;
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("127.0.0.1", 8888).start();
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建Bootstrap
            Bootstrap b = new Bootstrap();
            //指 定EventLoopGroup以处理客户端事件；需要适⽤于NIO的实现
            b.group(group)
                    //适⽤于NIO传输的Channel类型
                    .channel(NioSocketChannel.class)
                    //设置服务器的InetSocketAddr-ess
                    .remoteAddress(new InetSocketAddress(host, prot))
                    //在创建Channel时，向 ChannelPipeline中添加⼀个EchoClientHandler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //连接到远程节点，阻塞等待直到连接完成
            ChannelFuture f = b.connect().sync();
            //阻塞，直到Channel关闭
            f.channel().closeFuture().sync();
        } finally {
            //阻塞，直到Channel关闭
            group.shutdownGracefully().sync();
        }
    }
}
