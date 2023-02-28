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
package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import com.saucesubfresh.rpc.core.exception.UnSupportPacketException;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import com.saucesubfresh.rpc.core.utils.serialize.ProtostuffUtils;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.process.MessageProcess;
import com.saucesubfresh.rpc.server.registry.RegistryService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
    private final ServerConfiguration configuration;
    private final RegistryService registryService;

    public NettyMessageHandler(MessageProcess messageProcess, ServerConfiguration configuration, RegistryService registryService) {
        this.messageProcess = messageProcess;
        this.configuration = configuration;
        this.registryService = registryService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequest request) throws Exception {
        MessageResponseBody responseBody = new MessageResponseBody();
        String requestJsonBody = request.getBody();
        MessageRequestBody requestBody = JSON.parse(requestJsonBody, MessageRequestBody.class);
        responseBody.setRequestId(requestBody.getRequestId());
        responseBody.setServerId(requestBody.getServerId());
        Message message = requestBody.getMessage();
        PacketType command = message.getCommand();
        try {
            switch (command){
                case REGISTER:
                    registryService.register(configuration.getServerAddress(), configuration.getServerPort());
                    break;
                case DEREGISTER:
                    registryService.deRegister(configuration.getServerAddress(), configuration.getServerPort());
                    break;
                case MESSAGE:
                    byte[] body = messageProcess.process(message);
                    responseBody.setBody(body);
                    break;
                case PING:
                    responseBody.setBody(ProtostuffUtils.serialize(PacketType.PONG.name()));
                    break;
                default:
                    throw new UnSupportPacketException("UnSupport message packet" + command);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(ResponseStatus.ERROR);
        } finally {
            String responseJsonBody = JSON.toJSON(responseBody);
            MessageResponse response = MessageResponse.newBuilder().setBody(responseJsonBody).build();
            ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> log.info("Send response for request " + requestBody.getRequestId()));
        }
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
}