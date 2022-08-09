package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务端通道管理器
 */
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
    public static ManagedChannel establishChannel(ServerInformation information) {
        String serverId = information.getServerId();
        if (StringUtils.isBlank(serverId)) {
            throw new RpcException("Server" + serverId + " is not registered");
        }
        ManagedChannel channel = SERVER_CHANNEL.get(serverId);
        if (ObjectUtils.isEmpty(channel) || channel.isShutdown()) {
            channel = ManagedChannelBuilder.forAddress(information.getAddress(), information.getPort())
                    .usePlaintext()
                    .build();
            SERVER_CHANNEL.put(serverId, channel);
        }
        return channel;
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
