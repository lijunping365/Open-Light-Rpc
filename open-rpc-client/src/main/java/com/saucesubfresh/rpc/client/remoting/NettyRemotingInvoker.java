package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class NettyRemotingInvoker implements RemotingInvoker {

    private final NettyClient nettyClient;
    private final RequestIdGenerator requestIdGenerator;

    public NettyRemotingInvoker(NettyClient nettyClient, RequestIdGenerator requestIdGenerator) {
        this.nettyClient = nettyClient;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        String serverId = serverInformation.getServerId();
        Bootstrap bootstrap = nettyClient.getBootstrap();
        Channel channel = NettyClientChannelManager.establishChannel(bootstrap, serverInformation);
        try {
            final String random = requestIdGenerator.generate();
            MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
            String requestJsonBody = JSON.toJSON(requestBody);
            MessageRequest messageRequest = MessageRequest.newBuilder().setBody(requestJsonBody).build();
            ChannelFuture channelFuture = channel.writeAndFlush(messageRequest).sync();
            if (!channelFuture.isSuccess()) {
                log.error("Send request {} error", requestBody.getRequestId());
            }

            //MessageResponse response = channelFuture.ge();
            //return JSON.parse(response.getBody(), MessageResponseBody.class);
            return null;
        } catch (Exception e) {
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            log.error("flush error {}", socketAddress.getAddress().getHostAddress());
            NettyClientChannelManager.removeChannel(serverId);
            throw new RpcException(String.format("To the Server: %s, exception when sending a message", serverId));
        }
    }
}
