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
package com.openbytecode.rpc.client.config;

import com.openbytecode.rpc.client.random.RequestIdGenerator;
import com.openbytecode.rpc.client.ClientConfiguration;
import com.openbytecode.rpc.client.annotation.EnableOpenRpcClient;
import com.openbytecode.rpc.client.intercept.DefaultResponseInterceptor;
import com.openbytecode.rpc.client.intercept.RequestInterceptor;
import com.openbytecode.rpc.client.intercept.DefaultRequestInterceptor;
import com.openbytecode.rpc.client.intercept.ResponseInterceptor;
import com.openbytecode.rpc.client.random.support.SequenceRequestIdGenerator;
import com.openbytecode.rpc.client.remoting.GrpcClient;
import com.openbytecode.rpc.client.remoting.GrpcRemotingInvoker;
import com.openbytecode.rpc.client.remoting.RemotingInvoker;
import com.openbytecode.rpc.client.remoting.RemotingClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijunping on 2022/1/20
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ClientConfiguration.class)
@ConditionalOnBean(annotation = {EnableOpenRpcClient.class})
public class ClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestIdGenerator requestIdGenerator(){
        return new SequenceRequestIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingClient remotingClient(){
        return new GrpcClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor requestInterceptor(){
        return new DefaultRequestInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseInterceptor responseInterceptor(){
        return new DefaultResponseInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingInvoker remotingInvoker(RemotingClient remotingClient,
                                           RequestIdGenerator requestIdGenerator,
                                           RequestInterceptor requestInterceptor,
                                           ResponseInterceptor responseInterceptor){
        return new GrpcRemotingInvoker(remotingClient, requestIdGenerator, requestInterceptor, responseInterceptor);
    }

}
