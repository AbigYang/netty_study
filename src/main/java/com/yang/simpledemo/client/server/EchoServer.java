package com.yang.simpledemo.client.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Usage: " + EchoServer.class.getSimpleName() + " ");
//        }
        int port = 8888;
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建Event-LoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建Server-Bootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    //指定所使⽤的NIO传输Channel
                    .channel(NioServerSocketChannel.class)
                    //使⽤指定的端⼝设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //添加⼀个EchoServer-Handler到⼦Channel的ChannelPipeline
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ////EchoServerHandler被标注为@Shareable，所以我们可以总是使⽤同样的实例
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            //异步地绑定服务器；调⽤sync()⽅法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            //获取Channel的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            //关闭EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
