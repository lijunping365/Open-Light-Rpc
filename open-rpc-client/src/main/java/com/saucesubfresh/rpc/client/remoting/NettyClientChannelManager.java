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

import com.saucesubfresh.rpc.core.exception.ConnectNetworkException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务端通道管理器
 *
 * @author lijunping
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
    public static Channel establishChannel(NettyClient nettyClient, ServerInformation serverInformation) {
        String serverId = serverInformation.getServerId();
        Channel channel = SERVER_CHANNEL.get(serverId);
        if (!ObjectUtils.isEmpty(channel) && channel.isActive()){
            return channel;
        }

        try {
            channel = nettyClient.connect(serverInformation.getAddress(), serverInformation.getPort());
            SERVER_CHANNEL.put(serverId, channel);
            return channel;
        } catch (Exception e) {
            log.error("Failed to connect to the server {}, and exception is {}", serverId, e);
            throw new ConnectNetworkException(serverId, "Failed to connect to the server:" + serverId);
        }
    }

    /**
     * Remove client {@link Channel}
     *
     * @param serverId The client id
     */
    public static void removeChannel(String serverId) {
        SERVER_CHANNEL.remove(serverId);
    }

    /**
     * Get all serverChannel
     *
     * @return serverChannel
     */
    public static ConcurrentMap<String, Channel> getServerChannel() {
        return SERVER_CHANNEL;
    }
}