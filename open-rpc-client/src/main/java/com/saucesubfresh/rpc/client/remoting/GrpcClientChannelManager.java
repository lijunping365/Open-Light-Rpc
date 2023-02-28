/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.exception.NetworkException;
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
 *
 * @author lijunping
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
            log.error("Failed to connect to the server {}", serverId);
            throw new NetworkException("Failed to connect to the server:" + serverId);
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