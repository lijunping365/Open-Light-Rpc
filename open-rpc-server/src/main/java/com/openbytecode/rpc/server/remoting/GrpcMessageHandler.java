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
import com.openbytecode.rpc.core.grpc.MessageServiceGrpc;
import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.core.grpc.proto.MessageResponse;
import com.openbytecode.rpc.core.transport.MessageResponseBody;
import com.openbytecode.rpc.core.utils.json.JSON;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcMessageHandler extends MessageServiceGrpc.MessageServiceImplBase implements MessageHandler {

    private final MessageProcess messageProcess;

    public GrpcMessageHandler(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
    }

    @Override
    public void messageProcessing(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
        messageProcess.process(request, (t) -> writeResponse(t, responseObserver));
    }

    private void writeResponse(MessageResponseBody responseBody, StreamObserver<MessageResponse> responseObserver){
        String responseJsonBody = JSON.toJSON(responseBody);
        MessageResponse messageResponse = MessageResponse.newBuilder().setBody(responseJsonBody).build();
        responseObserver.onNext(messageResponse);
        responseObserver.onCompleted();
    }
}