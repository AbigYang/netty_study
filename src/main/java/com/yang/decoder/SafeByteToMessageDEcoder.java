package com.yang.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

public class SafeByteToMessageDEcoder extends ByteToMessageDecoder {
    private static final int MAX_FRAME_SIZE = 1024;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int bytes = in.readableBytes();
        if (bytes > MAX_FRAME_SIZE) {
            in.skipBytes(bytes);
            throw new TooLongFrameException("Frame too big!");
        }
        //do something
    }
}
