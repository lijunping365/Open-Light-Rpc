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
package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.callback.CallCallback;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RemoteInvokeException;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class NettyRemotingInvoker implements RemotingInvoker {

    private final RpcClient rpcClient;
    private final RequestIdGenerator requestIdGenerator;

    public NettyRemotingInvoker(RpcClient rpcClient, RequestIdGenerator requestIdGenerator) {
        this.rpcClient = rpcClient;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        Channel channel = NettyClientChannelManager.establishChannel((NettyClient) rpcClient, serverInformation);
        String serverId = serverInformation.getServerId();
        final String random = requestIdGenerator.generate();
        MessageRequest messageRequest = buildRequest(serverId, message, random);
        CompletableFuture<MessageResponseBody> completableFuture = new CompletableFuture<>();
        NettyUnprocessedRequests.put(random, completableFuture);
        try {
            channel.writeAndFlush(messageRequest).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    completableFuture.completeExceptionally(future.cause());
                }
            });
            return completableFuture.get();
        } catch (Exception e) {
            handlerException(serverId, channel, e);
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
    }

    @Override
    public void invokeAsync(Message message, ServerInformation serverInformation, CallCallback callback) throws RpcException {
        String serverId = serverInformation.getServerId();
        Channel channel = NettyClientChannelManager.establishChannel((NettyClient) rpcClient, serverInformation);
        final String random = requestIdGenerator.generate();
        MessageRequest messageRequest = buildRequest(serverId, message, random);
        CompletableFuture<MessageResponseBody> completableFuture = new CompletableFuture<>();
        NettyUnprocessedRequests.put(random, completableFuture);
        try {
            channel.writeAndFlush(messageRequest).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    MessageResponseBody responseBody = completableFuture.get();
                    callback.onResponse(responseBody);
                }else {
                    completableFuture.completeExceptionally(channelFuture.cause());
                }
            });
        } catch (Exception e) {
            handlerException(serverId, channel, e);
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
    }

    private MessageRequest buildRequest(String serverId, Message message, String random){
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        String requestJsonBody = JSON.toJSON(requestBody);
        return MessageRequest.newBuilder().setBody(requestJsonBody).build();
    }

    private void handlerException(String serverId, Channel channel, Exception e){
        log.error("To the server {}, occur exception {}", serverId, e.getMessage());
        if (!channel.isActive() && !channel.isOpen() && !channel.isWritable()) {
            channel.close();
            NettyClientChannelManager.removeChannel(serverId);
            log.error("The Server is unavailable, shutdown channel and the cached channel is deleted.");
        }
    }
}