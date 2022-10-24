/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
