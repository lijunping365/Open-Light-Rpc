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
package com.openbytecode.rpc.client.remoting;

import com.openbytecode.rpc.client.callback.CallCallback;
import com.openbytecode.rpc.client.random.RequestIdGenerator;
import com.openbytecode.rpc.client.intercept.RequestInterceptor;
import com.openbytecode.rpc.client.intercept.ResponseInterceptor;
import com.openbytecode.rpc.core.Message;
import com.openbytecode.rpc.core.exception.RemoteInvokeException;
import com.openbytecode.rpc.core.exception.RpcException;
import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.core.information.ServerInformation;
import com.openbytecode.rpc.core.transport.MessageRequestBody;
import com.openbytecode.rpc.core.transport.MessageResponseBody;
import com.openbytecode.rpc.core.utils.json.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class NettyRemotingInvoker implements RemotingInvoker {

    private final RemotingClient remotingClient;
    private final RequestIdGenerator requestIdGenerator;
    private final RequestInterceptor requestInterceptor;
    private final ResponseInterceptor responseInterceptor;

    public NettyRemotingInvoker(RemotingClient remotingClient, RequestIdGenerator requestIdGenerator, RequestInterceptor requestInterceptor, ResponseInterceptor responseInterceptor) {
        this.remotingClient = remotingClient;
        this.requestInterceptor = requestInterceptor;
        this.requestIdGenerator = requestIdGenerator;
        this.responseInterceptor = responseInterceptor;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        Channel channel = NettyClientChannelManager.establishChannel((NettyClient) remotingClient, serverInformation);
        String serverId = serverInformation.getServerId();
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        requestInterceptor.intercept(requestBody);
        String requestJsonBody = JSON.toJSON(requestBody);
        MessageRequest messageRequest =  MessageRequest.newBuilder().setBody(requestJsonBody).build();
        CompletableFuture<MessageResponseBody> completableFuture = new CompletableFuture<>();
        NettyUnprocessedRequests.put(random, completableFuture);
        try {
            channel.writeAndFlush(messageRequest).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    completableFuture.completeExceptionally(future.cause());
                }
            });
            MessageResponseBody responseBody = completableFuture.get();
            responseInterceptor.intercept(requestBody, responseBody);
            return responseBody;
        } catch (Exception e) {
            handlerException(serverId, channel, e);
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
    }

    @Override
    public void invokeAsync(Message message, ServerInformation serverInformation, CallCallback callback) throws RpcException {
        Channel channel = NettyClientChannelManager.establishChannel((NettyClient) remotingClient, serverInformation);
        String serverId = serverInformation.getServerId();
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        requestInterceptor.intercept(requestBody);
        MessageRequest messageRequest =  MessageRequest.newBuilder().setBody(JSON.toJSON(requestBody)).build();
        CompletableFuture<MessageResponseBody> completableFuture = new CompletableFuture<>();
        NettyUnprocessedRequests.put(random, completableFuture);
        try {
            channel.writeAndFlush(messageRequest).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    completableFuture.whenComplete((responseBody, throwable) -> {
                        responseInterceptor.intercept(requestBody, responseBody);
                    }).whenComplete((responseBody, throwable) -> {
                        if (throwable == null && callback != null){
                            callback.onCompleted(responseBody);
                        }
                    }).exceptionally(e->{
                        log.error(e.getMessage(), e);
                        throw new RemoteInvokeException(serverId, e.getMessage());
                    });
                } else {
                    completableFuture.completeExceptionally(channelFuture.cause());
                }
            });
        } catch (Exception e) {
            handlerException(serverId, channel, e);
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
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