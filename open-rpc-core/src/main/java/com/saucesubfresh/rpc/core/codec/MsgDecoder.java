package com.saucesubfresh.rpc.core.codec;

import com.saucesubfresh.rpc.core.utils.serialize.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lijunping on 2022/8/8
 */
public class MsgDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public MsgDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) {
        try {
            byte[] body = new byte[in.readableBytes()];
            in.readBytes(body);

            list.add(ProtostuffUtils.deserialize(body, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
