package com.lightcode.rpc.server.remoting.support;

import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.grpc.MessageServiceGrpc;
import com.lightcode.rpc.core.grpc.proto.MessageRequest;
import com.lightcode.rpc.core.grpc.proto.MessageResponse;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.core.utils.json.JSON;
import com.lightcode.rpc.core.transport.MessageRequestBody;
import com.lightcode.rpc.core.transport.MessageResponseBody;
import com.lightcode.rpc.core.transport.MessageResponseStatus;
import com.lightcode.rpc.server.random.RequestIdGenerator;
import com.lightcode.rpc.server.remoting.RemotingInvoker;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class GrpcRemotingInvoker implements RemotingInvoker {

    private final RequestIdGenerator requestIdGenerator;
    public GrpcRemotingInvoker(RequestIdGenerator requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
    }


    @Override
    public void invoke(Message message, ClientInformation clientInformation) throws RpcException {
        String clientId = clientInformation.getClientId();
        ManagedChannel channel = ClientChannelManager.establishChannel(clientInformation);
        try {
            MessageServiceGrpc.MessageServiceBlockingStub messageClientStub = MessageServiceGrpc.newBlockingStub(channel);
            final String random = requestIdGenerator.generate();
            MessageRequestBody requestBody = new MessageRequestBody().setClientId(clientId).setMessage(message).setRequestId(random);
            String requestJsonBody = JSON.toJSON(requestBody);
            MessageResponse response = messageClientStub.messageProcessing(MessageRequest.newBuilder().setBody(requestJsonBody).build());
            MessageResponseBody responseBody = JSON.parse(response.getBody(), MessageResponseBody.class);
            if (!MessageResponseStatus.SUCCESS.equals(responseBody.getStatus())) {
                log.error("To the client: {}, the message is sent abnormally, and the message is recovered.", clientId);
                throw new RpcException(String.format("To the client: %s, the message is sent abnormally, and the message is recovered.", clientId));
            }
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            log.error("To the client: {}, exception when sending a message, Status Code: {}", clientId, code);
            // The server status is UNAVAILABLE
            if (Status.Code.UNAVAILABLE == code) {
                ClientChannelManager.removeChannel(clientId);
                log.error("The client is unavailable, and the cached channel is deleted.");
            }
            throw new RpcException(String.format("To the client: %s, exception when sending a message, Status Code: %s", clientId, code));
        } catch (Exception e) {
            throw new RpcException("rpc failed:" + e.getMessage());
        }
    }
}
