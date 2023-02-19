package com.yang.coder;

import io.netty.channel.CombinedChannelDuplexHandler;

public class CombinedByteCharCodec extends
        CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CombinedByteCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
}
