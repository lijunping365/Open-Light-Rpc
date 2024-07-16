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
package com.openbytecode.rpc.client.remoting;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lijunping on 2022/8/23
 */
@Slf4j
public class GrpcClient extends AbstractRemotingClient {

    public ManagedChannel connect(String address, int port){
        return ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {
        ConcurrentMap<String, ManagedChannel> serverChannel = GrpcClientChannelManager.getServerChannel();
        if (serverChannel.size() == 0) {
            return;
        }

        for (Map.Entry<String, ManagedChannel> item: serverChannel.entrySet()) {
            try {
                item.getValue().shutdown();
                GrpcClientChannelManager.removeChannel(item.getKey());
            }catch (Exception e){
                log.error("ManagedChannel shutdown exception, ", e);
            }
        }
    }
}
