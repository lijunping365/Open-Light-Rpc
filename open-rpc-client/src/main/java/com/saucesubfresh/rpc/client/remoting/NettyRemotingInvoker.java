package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author lijunping on 2022/2/16
 */
@Slf4j
public class NettyRemotingInvoker implements RemotingInvoker {

    private final RpcClient rpcClient;
    private final RequestIdGenerator requestIdGenerator;

    public NettyRemotingInvoker(RpcClient rpcClient, RequestIdGenerator requestIdGenerator) {
        this.rpcClient = rpcClient;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException {
        CompletableFuture<MessageResponseBody> completableFuture = new CompletableFuture<>();
        String serverId = serverInformation.getServerId();
        final String random = requestIdGenerator.generate();
        Channel channel = NettyClientChannelManager.establishChannel((NettyClient) rpcClient, serverInformation);
        NettyUnprocessedRequests.put(random, completableFuture);
        MessageRequestBody requestBody = new MessageRequestBody().setServerId(serverId).setMessage(message).setRequestId(random);
        String requestJsonBody = JSON.toJSON(requestBody);
        MessageRequest messageRequest = MessageRequest.newBuilder().setBody(requestJsonBody).build();

        channel.writeAndFlush(messageRequest).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                completableFuture.completeExceptionally(future.cause());
                log.error("Send failed:", future.cause());
            }
        });

        try {
            MessageResponseBody responseBody = completableFuture.get();
            return JSON.parse(responseBody, MessageResponseBody.class);
        } catch (Exception e) {
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            log.error("flush error {}", socketAddress.getAddress().getHostAddress());
            if (!channel.isActive() && !channel.isOpen() && !channel.isWritable()) {
                channel.close();
                NettyClientChannelManager.removeChannel(serverId);
                log.error("The Server is unavailable, shutdown channel and the cached channel is deleted.");
            }
            throw new RpcException(String.format("To the Server: %s, exception when sending a message", serverId));
        }
    }
}
