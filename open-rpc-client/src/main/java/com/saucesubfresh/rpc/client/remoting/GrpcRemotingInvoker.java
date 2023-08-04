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
import com.saucesubfresh.rpc.client.intercept.RequestInterceptor;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RemoteInvokeException;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.MessageServiceGrpc;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class GrpcRemotingInvoker implements RemotingInvoker {

    private final RpcClient rpcClient;
    private final RequestInterceptor requestInterceptor;
    private final RequestIdGenerator requestIdGenerator;

    public GrpcRemotingInvoker(RpcClient rpcClient, RequestInterceptor requestInterceptor, RequestIdGenerator requestIdGenerator) {
        this.rpcClient = rpcClient;
        this.requestInterceptor = requestInterceptor;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        String serverId = serverInformation.getServerId();
        ManagedChannel channel = GrpcClientChannelManager.establishChannel((GrpcClient) rpcClient, serverInformation);
        MessageServiceGrpc.MessageServiceBlockingStub messageClientStub = MessageServiceGrpc.newBlockingStub(channel);
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        MessageRequest messageRequest = MessageRequest.newBuilder().setBody(JSON.toJSON(requestBody)).build();
        requestInterceptor.intercept(requestBody);

        try {
            MessageResponse response = messageClientStub.messageProcessing(messageRequest);
            return JSON.parse(response.getBody(), MessageResponseBody.class);
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            handlerException(serverId, channel, code);
            throw new RemoteInvokeException(serverId, e.getMessage());
        } catch (Exception e) {
            channel.shutdown();
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
    }

    @Override
    public void invokeAsync(Message message, ServerInformation serverInformation, CallCallback callback) throws RpcException {
        String serverId = serverInformation.getServerId();
        ManagedChannel channel = GrpcClientChannelManager.establishChannel((GrpcClient) rpcClient, serverInformation);
        MessageServiceGrpc.MessageServiceStub messageServiceStub = MessageServiceGrpc.newStub(channel);
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        MessageRequest messageRequest = MessageRequest.newBuilder().setBody(JSON.toJSON(requestBody)).build();
        requestInterceptor.intercept(requestBody);

        try {
            messageServiceStub.messageProcessing(messageRequest, new StreamObserver<MessageResponse>() {
                @Override
                public void onNext(MessageResponse response) {
                    MessageResponseBody responseBody = JSON.parse(response.getBody(), MessageResponseBody.class);
                    callback.onCompleted(responseBody);
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getMessage(), throwable);
                    throw new RemoteInvokeException(serverId, throwable.getMessage());
                }

                @Override
                public void onCompleted() {

                }
            });
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            handlerException(serverId, channel, code);
            throw new RemoteInvokeException(serverId, e.getMessage());
        } catch (Exception e) {
            channel.shutdown();
            String msg = String.format("To the Server: %s, exception when sending a message, cause by: %s", serverId, e.getMessage());
            throw new RemoteInvokeException(serverId, msg);
        }
    }

    private void handlerException(String serverId, ManagedChannel channel, Status.Code code){
        log.error("To the Server: {}, exception when sending a message, Status Code: {}", serverId, code);
        if (Status.Code.UNAVAILABLE == code) {
            channel.shutdown();
            GrpcClientChannelManager.removeChannel(serverId);
            log.error("The Server is unavailable, shutdown channel and the cached channel is deleted.");
        }
    }
}