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
package com.openbytecode.rpc.server.remoting;

import com.openbytecode.rpc.server.process.MessageProcess;
import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.core.grpc.proto.MessageResponse;
import com.openbytecode.rpc.core.transport.MessageResponseBody;
import com.openbytecode.rpc.core.utils.json.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 这里处理所有netty事件。
 *
 * @author lijunping 2022-06-08 08:04
 */
@Slf4j
@Sharable
public class NettyMessageHandler extends SimpleChannelInboundHandler<MessageRequest> implements MessageHandler{

    private final MessageProcess messageProcess;

    public NettyMessageHandler(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequest request) throws Exception {
        messageProcess.process(request, (t) -> writeResponse(t, ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("some thing is error , " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close(); // close if idle
            log.debug("netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void writeResponse(MessageResponseBody responseBody, ChannelHandlerContext ctx){
        String responseJsonBody = JSON.toJSON(responseBody);
        MessageResponse response = MessageResponse.newBuilder().setBody(responseJsonBody).build();
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> log.info("Send response for request " + responseBody.getRequestId()));
    }
}