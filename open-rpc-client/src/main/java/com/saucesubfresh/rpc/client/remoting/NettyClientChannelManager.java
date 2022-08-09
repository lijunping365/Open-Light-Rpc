package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务端通道管理器
 */
@Slf4j
public class NettyClientChannelManager {
    /**
     * Store the connection channel of each server
     * <p>
     * The key is {@link ServerInformation#getServerId()}
     */
    private static final ConcurrentMap<String, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>();

    private final NettyClient nettyClient;

    public NettyClientChannelManager(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * Establish a server channel
     *
     * @param serverInformation The {@link ServerInformation} instance
     * @return {@link Channel} instance
     */
    public Channel establishChannel(ServerInformation serverInformation) {

        if (ObjectUtils.isEmpty(serverInformation)) {
            throw new RpcException("serverInformation is not registered");
        }

        String clientId = serverInformation.getServerId();
        Channel channel = CHANNEL_CACHE.get(clientId);

        if (!ObjectUtils.isEmpty(channel) && channel.isActive()){
            return channel;
        }

        Bootstrap bootstrap = nettyClient.getBootstrap();
        try {
            ChannelFuture channelFuture = bootstrap.connect(serverInformation.getAddress(), serverInformation.getPort()).sync();
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
