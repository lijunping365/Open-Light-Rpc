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
package com.openbytecode.rpc.server.config;

import com.openbytecode.rpc.server.ServerConfiguration;
import com.openbytecode.rpc.server.annotation.EnableOpenRpcServer;
import com.openbytecode.rpc.server.hook.DefaultShutdownHook;
import com.openbytecode.rpc.server.hook.ShutdownHook;
import com.openbytecode.rpc.server.remoting.GrpcMessageHandler;
import com.openbytecode.rpc.server.remoting.GrpcServer;
import com.openbytecode.rpc.server.remoting.MessageHandler;
import com.openbytecode.rpc.server.remoting.RemotingServer;
import com.openbytecode.rpc.server.remoting.*;
import com.openbytecode.rpc.server.process.DefaultMessageProcess;
import com.openbytecode.rpc.server.process.MessageProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijunping on 2022/1/20
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ServerConfiguration.class)
@ConditionalOnBean(annotation = {EnableOpenRpcServer.class})
public class ServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageHandler messageHandler(MessageProcess messageProcess){
        return new GrpcMessageHandler(messageProcess);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShutdownHook shutdownHook(){
        return new DefaultShutdownHook();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingServer remotingServer(ShutdownHook shutdownHook,
                                         MessageHandler messageHandler,
                                         ServerConfiguration configuration){
        return new GrpcServer(shutdownHook, configuration, messageHandler);
    }
}
