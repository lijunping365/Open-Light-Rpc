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
import com.openbytecode.rpc.client.intercept.RequestInterceptor;
import com.openbytecode.rpc.client.intercept.ResponseInterceptor;
import com.openbytecode.rpc.client.random.RequestIdGenerator;
import com.openbytecode.rpc.core.Message;
import com.openbytecode.rpc.core.exception.RemoteInvokeException;
import com.openbytecode.rpc.core.exception.RpcException;
import com.openbytecode.rpc.core.grpc.MessageServiceGrpc;
import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.core.grpc.proto.MessageResponse;
import com.openbytecode.rpc.core.information.ServerInformation;
import com.openbytecode.rpc.core.transport.MessageRequestBody;
import com.openbytecode.rpc.core.transport.MessageResponseBody;
import com.openbytecode.rpc.core.utils.json.JSON;
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

    private final RemotingClient remotingClient;
    private final RequestIdGenerator requestIdGenerator;
    private final RequestInterceptor requestInterceptor;
    private final ResponseInterceptor responseInterceptor;

    public GrpcRemotingInvoker(RemotingClient remotingClient, RequestIdGenerator requestIdGenerator, RequestInterceptor requestInterceptor, ResponseInterceptor responseInterceptor) {
        this.remotingClient = remotingClient;
        this.requestInterceptor = requestInterceptor;
        this.requestIdGenerator = requestIdGenerator;
        this.responseInterceptor = responseInterceptor;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        String serverId = serverInformation.getServerId();
        ManagedChannel channel = GrpcClientChannelManager.establishChannel((GrpcClient) remotingClient, serverInformation);
        MessageServiceGrpc.MessageServiceBlockingStub messageClientStub = MessageServiceGrpc.newBlockingStub(channel);
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        requestInterceptor.intercept(requestBody);
        MessageRequest messageRequest = MessageRequest.newBuilder().setBody(JSON.toJSON(requestBody)).build();

        try {
            MessageResponse response = messageClientStub.messageProcessing(messageRequest);
            MessageResponseBody responseBody = JSON.parse(response.getBody(), MessageResponseBody.class);
            responseInterceptor.intercept(requestBody, responseBody);
            return responseBody;
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
        ManagedChannel channel = GrpcClientChannelManager.establishChannel((GrpcClient) remotingClient, serverInformation);
        MessageServiceGrpc.MessageServiceStub messageServiceStub = MessageServiceGrpc.newStub(channel);
        final String random = requestIdGenerator.generate();
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        requestInterceptor.intercept(requestBody);
        MessageRequest messageRequest = MessageRequest.newBuilder().setBody(JSON.toJSON(requestBody)).build();

        try {
            messageServiceStub.messageProcessing(messageRequest, new StreamObserver<MessageResponse>() {
                @Override
                public void onNext(MessageResponse response) {
                    MessageResponseBody responseBody = JSON.parse(response.getBody(), MessageResponseBody.class);
                    responseInterceptor.intercept(requestBody, responseBody);
                    if (callback != null){
                        callback.onCompleted(responseBody);
                    }
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