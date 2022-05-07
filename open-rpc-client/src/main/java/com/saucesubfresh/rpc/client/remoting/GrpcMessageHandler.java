package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.process.MessageProcess;
import com.saucesubfresh.rpc.client.registry.RegistryService;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.MessageServiceGrpc;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
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
        String requestJsonBody = request.getBody();
        MessageRequestBody requestBody = JSON.parse(requestJsonBody, MessageRequestBody.class);
        Message message = requestBody.getMessage();
        PacketType command = message.getCommand();
        Message response = null;
        try {
            switch (command){
                case REGISTER:
                    registryService.register(configuration.getServerAddress(), configuration.getServerPort());
                    response = new Message().setSuccess(true);
                    break;
                case DEREGISTER:
                    String clientId = requestBody.getClientId();
                    String[] clientInfo = StringUtils.split(clientId, CommonConstant.Symbol.DOUBLE_COLON);
                    registryService.deRegister(clientInfo[0], Integer.parseInt(clientInfo[1]));
                    response = new Message().setSuccess(true);
                    break;
                case MESSAGE:
                    response = messageProcess.process(message);
                    break;
                default:
                    throw new RpcException("UnSupport message packet" + command);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            responseBody.setResponseBody(response);
            String responseJsonBody = JSON.toJSON(responseBody);
            MessageResponse messageResponse = MessageResponse.newBuilder().setBody(responseJsonBody).build();
            responseObserver.onNext(messageResponse);
            responseObserver.onCompleted();
        }
    }
}
