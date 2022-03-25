package com.lightcode.rpc.client.remoting;

import com.lightcode.rpc.client.ClientConfiguration;
import com.lightcode.rpc.client.process.MessageProcess;
import com.lightcode.rpc.client.registry.RegistryService;
import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.enums.PacketType;
import com.lightcode.rpc.core.constants.CommonConstant;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.grpc.MessageServiceGrpc;
import com.lightcode.rpc.core.grpc.proto.MessageRequest;
import com.lightcode.rpc.core.grpc.proto.MessageResponse;
import com.lightcode.rpc.core.utils.json.JSON;
import com.lightcode.rpc.core.transport.MessageRequestBody;
import com.lightcode.rpc.core.transport.MessageResponseBody;
import com.lightcode.rpc.core.transport.MessageResponseStatus;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcMessageHandler extends MessageServiceGrpc.MessageServiceImplBase{

    private final MessageProcess messageProcess;
    private final ClientConfiguration configuration;
    private final RegistryService registryService;

    public GrpcMessageHandler(MessageProcess messageProcess, ClientConfiguration configuration, RegistryService registryService) {
        this.messageProcess = messageProcess;
        this.configuration = configuration;
        this.registryService = registryService;
    }

    @Override
    public void messageProcessing(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
        MessageResponseBody responseBody = new MessageResponseBody();
        try {
            String requestJsonBody = request.getBody();
            MessageRequestBody requestBody = JSON.parse(requestJsonBody, MessageRequestBody.class);
            Message message = requestBody.getMessage();
            PacketType command = message.getCommand();
            boolean result;
            switch (command){
                case REGISTER:
                    result = registryService.register(configuration.getServerAddress(), configuration.getServerPort());
                    break;
                case DEREGISTER:
                    String clientId = requestBody.getClientId();
                    String[] clientInfo = StringUtils.split(clientId, CommonConstant.Symbol.DOUBLE_COLON);
                    result = registryService.deRegister(clientInfo[0], Integer.parseInt(clientInfo[1]));
                    break;
                case MESSAGE:
                    result = messageProcess.process(message);
                    break;
                default:
                    throw new RpcException("UnSupport message packet" + command);
            }
            responseBody.setStatus(result ? MessageResponseStatus.SUCCESS : MessageResponseStatus.ERROR);
        } catch (Exception e) {
            responseBody.setStatus(MessageResponseStatus.ERROR);
            log.error(e.getMessage(), e);
        } finally {
            String responseJsonBody = JSON.toJSON(responseBody);
            MessageResponse response = MessageResponse.newBuilder().setBody(responseJsonBody).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
