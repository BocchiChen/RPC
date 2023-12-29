package com.cxy.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RpcMessageEncoder<T> extends MessageToByteEncoder<T> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, T t, ByteBuf byteBuf) throws Exception {
        if(!(t instanceof String)){
            return;
        }
        byte[] contentBytes = ((String) t).getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(contentBytes.length);
        byteBuf.writeBytes(contentBytes);
    }
}
