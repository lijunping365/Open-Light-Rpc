package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务端通道管理器
 */
public class ClientChannelManager {
    /**
     * Store the connection channel of each client
     * <p>
     * The key is {@link ServerInformation#getClientId()}
     */
    private static final ConcurrentMap<String, ManagedChannel> CLIENT_CHANNEL = new ConcurrentHashMap<>();

    /**
     * Establish a client channel
     *
     * @param information The {@link ServerInformation} instance
     * @return {@link ManagedChannel} instance
     */
    public static ManagedChannel establishChannel(ServerInformation information) {
        String clientId = information.getClientId();
        if (ObjectUtils.isEmpty(information)) {
            throw new RpcException("Client: " + clientId + " is not registered");
        }
        ManagedChannel channel = CLIENT_CHANNEL.get(clientId);
        if (ObjectUtils.isEmpty(channel) || channel.isShutdown()) {
            channel = ManagedChannelBuilder.forAddress(information.getAddress(), information.getPort())
                    .usePlaintext()
                    .build();
            CLIENT_CHANNEL.put(clientId, channel);
        }
        return channel;
    }

    /**
     * Remove client {@link ManagedChannel}
     *
     * @param clientId The client id
     */
    public static void removeChannel(String clientId) {
        CLIENT_CHANNEL.remove(clientId);
    }
}
