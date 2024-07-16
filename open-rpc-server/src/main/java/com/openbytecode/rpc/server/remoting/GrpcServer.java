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
package com.openbytecode.rpc.server.remoting;

import com.openbytecode.rpc.server.ServerConfiguration;
import com.openbytecode.rpc.server.hook.ShutdownHook;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcServer extends AbstractRemotingServer {
    /**
     * The grpc server instance
     */
    private final Server rpcServer;
    private final MessageHandler messageHandler;
    private final ServerConfiguration configuration;

    public GrpcServer(ShutdownHook shutdownHook, ServerConfiguration configuration, MessageHandler messageHandler){
        super(shutdownHook);
        this.configuration = configuration;
        this.messageHandler = messageHandler;
        this.rpcServer = buildServer();
    }

    /**
     * Startup grpc {@link Server}
     */
    @Override
    public void start() {
        try {
            this.rpcServer.start();
            log.info("The GrpcServer bind port : {}, startup successfully.", configuration.getServerPort());
            this.rpcServer.awaitTermination();
        } catch (Exception e) {
            log.error("The GrpcServer startup failed.", e);
        }
    }

    /**
     * Shutdown grpc {@link Server}
     */
    @Override
    public void shutdown() {
        try {
            this.rpcServer.shutdown();
            log.info("The GrpcServer stop successfully.");
        } catch (Exception e) {
            log.error("The GrpcServer shutdown failed.", e);
        }
    }

    /**
     * Build the grpc {@link Server} instance
     */
    private Server buildServer() {
        int serverPort = configuration.getServerPort();
        return ServerBuilder.forPort(serverPort).addService((BindableService) this.messageHandler).build();
    }
}