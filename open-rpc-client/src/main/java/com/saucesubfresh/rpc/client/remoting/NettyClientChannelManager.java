package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

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
    private static final ConcurrentMap<String, Channel> SERVER_CHANNEL = new ConcurrentHashMap<>();

    /**
     * Establish a server channel
     *
     * @param serverInformation The {@link ServerInformation} instance
     * @return {@link Channel} instance
     */
    public static Channel establishChannel(Bootstrap bootstrap, ServerInformation serverInformation) {
        String serverId = serverInformation.getServerId();
        if (StringUtils.isBlank(serverId)) {
            throw new RpcException("Server" + serverId + " is not registered");
        }

        Channel channel = SERVER_CHANNEL.get(serverId);
        if (!ObjectUtils.isEmpty(channel) && channel.isActive()){
            return channel;
        }

        try {
            channel = connect(bootstrap, serverInformation.getAddress(), serverInformation.getPort());
        } catch (Exception e) {
            log.error("连接服务端失败 {}", serverId);
            throw new RpcException("连接服务端失败:" + serverId);
        }

        SERVER_CHANNEL.put(serverId, channel);
        return channel;
    }

    /**
     * connect sync
     */
    public static Channel connect(Bootstrap bootstrap, String address, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
        return channelFuture.channel();
    }

    /**
     * connect async
     */
    public static Channel connectAsync(Bootstrap bootstrap, String address, int port) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("Connect to server [{}:{}] successful!", address, port);
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    /**
     * Remove client {@link Channel}
     *
     * @param serverId The client id
     */
    public static void removeChannel(String serverId) {
        SERVER_CHANNEL.remove(serverId);
    }
}
