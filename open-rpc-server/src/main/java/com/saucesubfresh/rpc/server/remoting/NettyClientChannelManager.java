package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import io.grpc.ManagedChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 客户端通道管理器
 */
@Slf4j
public class NettyClientChannelManager {
    /**
     * Store the connection channel of each client
     * <p>
     * The key is {@link ClientInformation#getClientId()}
     */
    private static final ConcurrentMap<String, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>();

    private final NettyClient nettyClient;

    public NettyClientChannelManager(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * Establish a client channel
     *
     * @param clientInformation The {@link ClientInformation} instance
     * @return {@link ManagedChannel} instance
     */
    public Channel establishChannel(ClientInformation clientInformation) {

        if (ObjectUtils.isEmpty(clientInformation)) {
            throw new RpcException("clientInformation is not registered");
        }

        String clientId = clientInformation.getClientId();
        Channel channel = CHANNEL_CACHE.get(clientId);

        if (!ObjectUtils.isEmpty(channel) && channel.isActive()){
            return channel;
        }

        Bootstrap bootstrap = nettyClient.getBootstrap();
        try {
            ChannelFuture channelFuture = bootstrap.connect(clientInformation.getAddress(), clientInformation.getPort()).sync();
            channel = channelFuture.channel();
            CHANNEL_CACHE.put(clientId, channel);
            return channel;
        } catch (Exception e) {
            log.error("连接服务端失败 {}", clientId);
            throw new RpcException("连接服务端失败" + clientId);
        }
    }

    /**
     * Remove client {@link Channel}
     *
     * @param clientId The client id
     */
    public static void removeChannel(String clientId) {
        CHANNEL_CACHE.remove(clientId);
    }
}
