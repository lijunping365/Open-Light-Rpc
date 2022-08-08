package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.process.MessageProcess;
import com.saucesubfresh.rpc.client.registry.RegistryService;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 这里处理所有netty事件。
 * @author: 李俊平
 * @Date: 2022-06-08 08:04
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageRequest> {

    private final MessageProcess messageProcess;
    private final ClientConfiguration configuration;
    private final RegistryService registryService;

    public NettyClientHandler(MessageProcess messageProcess, ClientConfiguration configuration, RegistryService registryService) {
        this.messageProcess = messageProcess;
        this.configuration = configuration;
        this.registryService = registryService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequest request) throws Exception {
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
                    String clientId = requestBody.getClientId();
                    String[] clientInfo = StringUtils.split(clientId, CommonConstant.Symbol.DOUBLE_COLON);
                    registryService.deRegister(clientInfo[0], Integer.parseInt(clientInfo[1]));
                    break;
                case MESSAGE:
                    byte[] body = messageProcess.process(message);
                    responseBody.setBody(body);
                    break;
                default:
                    throw new RpcException("UnSupport message packet" + command);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseBody.setErrorMsg(e.getMessage());
            responseBody.setStatus(ResponseStatus.ERROR);
        } finally {
            String responseJsonBody = JSON.toJSON(responseBody);
            MessageResponse response = MessageResponse.newBuilder().setBody(responseJsonBody).build();
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("Send response for request " + requestBody.getRequestId());
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("some thing is error , " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        super.channelInactive(ctx);
    }
}
