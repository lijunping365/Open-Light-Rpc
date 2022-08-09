package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.MessageServiceGrpc;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
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
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        String clientId = serverInformation.getClientId();
        ManagedChannel channel = ClientChannelManager.establishChannel(serverInformation);
        try {
            MessageServiceGrpc.MessageServiceBlockingStub messageClientStub = MessageServiceGrpc.newBlockingStub(channel);
            final String random = requestIdGenerator.generate();
            MessageRequestBody requestBody = new MessageRequestBody().setClientId(clientId).setMessage(message).setRequestId(random);
            String requestJsonBody = JSON.toJSON(requestBody);
            MessageResponse response = messageClientStub.messageProcessing(MessageRequest.newBuilder().setBody(requestJsonBody).build());
            return JSON.parse(response.getBody(), MessageResponseBody.class);
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
