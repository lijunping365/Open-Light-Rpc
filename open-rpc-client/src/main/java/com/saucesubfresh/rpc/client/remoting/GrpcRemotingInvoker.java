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

    private final RpcClient rpcClient;
    private final RequestIdGenerator requestIdGenerator;

    public GrpcRemotingInvoker(RpcClient rpcClient, RequestIdGenerator requestIdGenerator) {
        this.rpcClient = rpcClient;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        String serverId = serverInformation.getServerId();
        ManagedChannel channel = GrpcClientChannelManager.establishChannel((GrpcClient) rpcClient, serverInformation);
        try {
            MessageServiceGrpc.MessageServiceBlockingStub messageClientStub = MessageServiceGrpc.newBlockingStub(channel);
            final String random = requestIdGenerator.generate();
            MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
            String requestJsonBody = JSON.toJSON(requestBody);
            MessageResponse response = messageClientStub.messageProcessing(MessageRequest.newBuilder().setBody(requestJsonBody).build());
            return JSON.parse(response.getBody(), MessageResponseBody.class);
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            log.error("To the Server: {}, exception when sending a message, Status Code: {}", serverId, code);
            if (Status.Code.UNAVAILABLE == code) {
                channel.shutdown();
                GrpcClientChannelManager.removeChannel(serverId);
                log.error("The Server is unavailable, shutdown channel and the cached channel is deleted.");
            }
            throw new RpcException(String.format("To the Server: %s, exception when sending a message, Status Code: %s", serverId, code));
        } catch (Exception e) {
            channel.shutdown();
            throw new RpcException("rpc failed:" + e.getMessage());
        }
    }
}