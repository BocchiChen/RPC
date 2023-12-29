package com.cxy.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RpcMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4){
            return;
        }
        int length = byteBuf.readInt();
        if(byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] contentBytes = new byte[length];
        byteBuf.readBytes(contentBytes);

        String message = new String(contentBytes, StandardCharsets.UTF_8);
        list.add(message);
    }
}
