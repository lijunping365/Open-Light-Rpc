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
package com.saucesubfresh.rpc.client.config;

import com.alibaba.nacos.api.naming.NamingService;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.annotation.EnableOpenRpcClient;
import com.saucesubfresh.rpc.client.cluster.ClusterInvoker;
import com.saucesubfresh.rpc.client.cluster.support.FailoverClusterInvoker;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.discovery.support.NacosServiceDiscovery;
import com.saucesubfresh.rpc.client.intercept.DefaultResponseInterceptor;
import com.saucesubfresh.rpc.client.intercept.RequestInterceptor;
import com.saucesubfresh.rpc.client.intercept.DefaultRequestInterceptor;
import com.saucesubfresh.rpc.client.intercept.ResponseInterceptor;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.loadbalance.support.ConsistentHashLoadBalance;
import com.saucesubfresh.rpc.client.manager.DefaultInstanceManager;
import com.saucesubfresh.rpc.client.manager.InstanceManager;
import com.saucesubfresh.rpc.client.namespace.DefaultNamespaceService;
import com.saucesubfresh.rpc.client.namespace.NamespaceService;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.client.random.support.SequenceRequestIdGenerator;
import com.saucesubfresh.rpc.client.remoting.GrpcClient;
import com.saucesubfresh.rpc.client.remoting.GrpcRemotingInvoker;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.client.remoting.RemotingClient;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.client.store.support.LocalInstanceStore;
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
    public InstanceStore instanceStore(){
        return new LocalInstanceStore();
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

    @Bean
    @ConditionalOnMissingBean
    public InstanceManager instanceManager(RemotingInvoker remotingInvoker){
        return new DefaultInstanceManager(remotingInvoker);
    }

    @Bean
    @ConditionalOnMissingBean
    public NamespaceService namespaceService(ClientConfiguration configuration){
        return new DefaultNamespaceService(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(NamingService.class)
    public ServiceDiscovery serviceDiscovery(NamingService namingService,
                                             InstanceStore instanceStore,
                                             NamespaceService namespaceService){
        return new NacosServiceDiscovery(namingService, instanceStore, namespaceService);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalance loadBalance(){
        return new ConsistentHashLoadBalance();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClusterInvoker clusterInvoker(ServiceDiscovery serviceDiscovery,
                                         ClientConfiguration configuration,
                                         LoadBalance loadBalance,
                                         RemotingInvoker remotingInvoker){
        return new FailoverClusterInvoker(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

}
