package com.yang.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    public SslChannelInitializer(SslContext context, boolean startTls) {
        //如果设置为true，第⼀个写⼊的消息将不会被加密（客户端应该设置为true）
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        //对于每个SslHandler实例，都使⽤Channel的ByteBuf-Allocator  从SslContext 获取⼀个新的SSLEngine
        SSLEngine engine = context.newEngine(ch.alloc());
        //将SslHandler 作为第⼀个ChannelHandler 添加到ChannelPipeline 中
        ch.pipeline().addFirst("ssl", new SslHandler(engine,  startTls));
    }
}
