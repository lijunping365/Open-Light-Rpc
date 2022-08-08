package com.saucesubfresh.rpc.core.codec;

import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.utils.serialize.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * @author lijunping on 2022/8/8
 */
public class MsgEncoder extends MessageToByteEncoder<Object> {

    private Class<?> clazz;

    public MsgEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (clazz.isInstance(in)) {
            byte[] bytes = ProtostuffUtils.serialize(in);
            byte[] delimiter = CommonConstant.DELIMITER.getBytes();

            byte[] total = new byte[bytes.length + delimiter.length];
            System.arraycopy(bytes, 0, total, 0, bytes.length);
            System.arraycopy(delimiter, 0, total, bytes.length, delimiter.length);

            out.writeBytes(total);
        }
    }
}
