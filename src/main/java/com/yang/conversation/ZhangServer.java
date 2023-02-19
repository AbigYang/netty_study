package com.yang.conversation;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 服务端是张大爷，客户端是李大爷，我们让俩人在胡同口碰见一百万次，记录下总共的耗时
 * private static final String Z0 = "吃了没，您吶?";
 * private static final String L1 = "刚吃。";
 * <p>
 * private static final String L2 = "您这，嘛去？";
 * private static final String Z3 = "嗨！吃饱了溜溜弯儿。";
 * <p>
 * private static final String L4 = "有空家里坐坐啊。";
 * private static final String Z5 = "回头去给老太太请安！";
 */
public class ZhangServer {

    private static final int MEET_COUNT = 100000;

    private static final String Z0 = "吃了没，您吶?";
    private static final String L1 = "刚吃。";

    private static final String L2 = "您这，嘛去？";
    private static final String Z3 = "嗨！吃饱了溜溜弯儿。";

    private static final String L4 = "有空家里坐坐啊。";
    private static final String Z5 = "回头去给老太太请安！";

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(999)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4, false))
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            super.channelActive(ctx);
                                            System.out.println("连接成功");
//                                            ctx.channel().writeAndFlush(Z0);
                                            new Thread(() -> {
                                                for (int i = 0; i < MEET_COUNT; i++) {
                                                    ctx.channel().writeAndFlush(Z0);
                                                }
                                            }).start();
                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            Channel channel = ctx.channel();
//                                            System.out.println(msg);
                                            switch (msg) {
                                                case L2:
                                                    channel.writeAndFlush(Z3);
                                                    break;
                                                case L4:
                                                    channel.writeAndFlush(Z5);
                                                    break;
                                            }
                                        }
                                    });
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }
}
