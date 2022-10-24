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
package com.saucesubfresh.rpc.server.config;

import com.alibaba.nacos.api.naming.NamingService;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.annotation.EnableOpenRpcServer;
import com.saucesubfresh.rpc.server.registry.support.NacosRegistryService;
import com.saucesubfresh.rpc.server.remoting.*;
import com.saucesubfresh.rpc.server.process.DefaultMessageProcess;
import com.saucesubfresh.rpc.server.process.MessageProcess;
import com.saucesubfresh.rpc.server.registry.RegistryService;
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
    @ConditionalOnBean(NamingService.class)
    public RegistryService registryService(NamingService namingService,
                                           ServerConfiguration configuration){
        return new NacosRegistryService(namingService, configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageHandler messageHandler(RegistryService registryService,
                                         MessageProcess messageProcess,
                                         ServerConfiguration configuration){
        return new GrpcMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcServer server(MessageHandler messageHandler,
                            ServerConfiguration configuration){
        return new GrpcServer(configuration, messageHandler);
    }
}
