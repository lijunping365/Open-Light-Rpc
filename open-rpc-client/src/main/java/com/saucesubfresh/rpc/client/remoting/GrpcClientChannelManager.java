package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务端通道管理器
 */
@Slf4j
public class GrpcClientChannelManager {
    /**
     * Store the connection channel of each client
     * <p>
     * The key is {@link ServerInformation#getServerId()}
     */
    private static final ConcurrentMap<String, ManagedChannel> SERVER_CHANNEL = new ConcurrentHashMap<>();

    /**
     * Establish a server channel
     *
     * @param information The {@link ServerInformation} instance
     * @return {@link ManagedChannel} instance
     */
    public static ManagedChannel establishChannel(GrpcClient grpcClient, ServerInformation information) {
        String serverId = information.getServerId();
        if (StringUtils.isBlank(serverId)) {
            throw new RpcException("Server" + serverId + " is not registered");
        }
        ManagedChannel channel = SERVER_CHANNEL.get(serverId);
        if (!ObjectUtils.isEmpty(channel) && !channel.isShutdown()) {
            return channel;
        }

        try {
            channel = grpcClient.connect(information.getAddress(), information.getPort());
            SERVER_CHANNEL.put(serverId, channel);
            return channel;
        }catch (Exception e){
            log.error("连接服务端失败 {}", serverId);
            throw new RpcException("连接服务端失败:" + serverId);
        }
    }

    /**
     * Remove client {@link ManagedChannel}
     *
     * @param serverId The serverId
     */
    public static void removeChannel(String serverId) {
        SERVER_CHANNEL.remove(serverId);
    }
}