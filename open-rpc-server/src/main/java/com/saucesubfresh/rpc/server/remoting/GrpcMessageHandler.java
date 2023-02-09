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
import com.saucesubfresh.rpc.core.grpc.MessageServiceGrpc;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.process.MessageProcess;
import com.saucesubfresh.rpc.server.registry.RegistryService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcMessageHandler extends MessageServiceGrpc.MessageServiceImplBase implements MessageHandler {

    private final MessageProcess messageProcess;
    private final ServerConfiguration configuration;
    private final RegistryService registryService;

    public GrpcMessageHandler(MessageProcess messageProcess, ServerConfiguration configuration, RegistryService registryService) {
        this.messageProcess = messageProcess;
        this.configuration = configuration;
        this.registryService = registryService;
    }

    @Override
    public void messageProcessing(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
        MessageResponseBody responseBody = new MessageResponseBody();
        String requestJsonBody = request.getBody();
        MessageRequestBody requestBody = JSON.parse(requestJsonBody, MessageRequestBody.class);
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
                default:
                    throw new UnSupportPacketException("UnSupport message packet" + command);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseBody.setStatus(ResponseStatus.ERROR);
        } finally {
            responseBody.setRequestId(requestBody.getRequestId());
            String responseJsonBody = JSON.toJSON(responseBody);
            MessageResponse messageResponse = MessageResponse.newBuilder().setBody(responseJsonBody).build();
            responseObserver.onNext(messageResponse);
            responseObserver.onCompleted();
        }
    }
}