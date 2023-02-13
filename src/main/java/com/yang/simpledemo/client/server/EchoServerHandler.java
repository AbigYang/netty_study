package com.yang.simpledemo.client.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        //将接收到的消息写给发送者，⽽不冲刷出站消息
        ctx.write(in/*.toString(CharsetUtil.UTF_8)*/);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将未决消息冲刷到远程节点，并且关闭该Channel
        //  未 决 消 息 （ pending message ） 是 指 ⽬ 前 暂 存 于ChannelOutboundBuffer 中 的 消 息 ，
        //  在 下 ⼀ 次 调 ⽤ flush() 或 者writeAndFlush()⽅法时将会尝试写出到套接字
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭该Channel
        ctx.close();
    }
}
