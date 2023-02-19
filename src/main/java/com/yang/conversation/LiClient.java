package com.yang.conversation;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class LiClient {
    private static final int MEET_COUNT = 100000;
    private static final String Z0 = "吃了没，您吶?";
    private static final String L1 = "刚吃。";

    private static final String L2 = "您这，嘛去？";
    private static final String Z3 = "嗨！吃饱了溜溜弯儿。";

    private static final String L4 = "有空家里坐坐啊。";
    private static final String Z5 = "回头去给老太太请安！";

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("127.0.0.1", 999)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4, false))
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new SimpleChannelInboundHandler<String>() {
                                        private int count = 0;
                                        private long start;

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            super.channelActive(ctx);
                                            System.out.println("连接成功");
                                            start = System.currentTimeMillis();
                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            Channel channel = ctx.channel();
//                                            System.out.println(msg);
                                            switch (msg) {
                                                case Z0:
                                                    channel.write(L1);
                                                    channel.writeAndFlush(L2);
                                                    break;
                                                case Z3:
                                                    channel.writeAndFlush(L4);
                                                    break;
                                                case Z5:
                                                    count++;
                                                    if (count == MEET_COUNT) {
                                                        System.out.println(System.currentTimeMillis() - start);
                                                    }
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                        }
                    });
            ChannelFuture future = b.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }
}
