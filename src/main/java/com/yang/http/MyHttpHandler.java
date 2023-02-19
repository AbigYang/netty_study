package com.yang.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class MyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println(msg);
        if (msg.method() == HttpMethod.GET && msg.uri().equals("/test")) {
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello,i am http server base on netty!", CharsetUtil.UTF_8);
            DefaultHttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf8")
                    .add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.write(response);
            ctx.write(byteBuf);
            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
