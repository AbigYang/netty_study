package com.yang.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //检查是否⾄少有4字节可读（⼀个int的字节⻓度）
        if (in.readableBytes() >= 4) {
            //从⼊站ByteBuf 中读取⼀个int，并将其添加到解码消息的List 中
            out.add(in.readInt());
        }
    }
}
