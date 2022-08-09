package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.information.ClientInformation;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: 李俊平
 * @Date: 2021-10-31 09:04
 */
public class ServerChannelManager {

    /**
     * The channel corresponding to each server
     */
    private static final ConcurrentMap<String, ManagedChannel> SERVER_CHANNEL = new ConcurrentHashMap<>();


    /**
     * Establish a channel with the server
     *
     * @param information ServerInformation
     * @return {@link ManagedChannel} instance
     */
    public static ManagedChannel establishChannel(ClientInformation information) {
        String serverId = information.getServerId();
        ManagedChannel channel = SERVER_CHANNEL.get(serverId);
        if (ObjectUtils.isEmpty(channel)) {
            channel = ManagedChannelBuilder.forAddress(information.getAddress(), information.getPort())
                    .usePlaintext()
                    .build();
            SERVER_CHANNEL.put(serverId, channel);
        }
        return channel;
    }

    /**
     * Delete unavailable server connection channel
     * <p>
     * If an unavailable Status appears during access after the connection is established,
     * delete the cached channel through this method
     *
     * @param serverId The serverId
     */
    public static void removeChannel(String serverId) {
        SERVER_CHANNEL.remove(serverId);
    }
}
