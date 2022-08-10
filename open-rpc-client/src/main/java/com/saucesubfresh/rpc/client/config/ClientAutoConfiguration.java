package com.saucesubfresh.rpc.client.config;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.cluster.ClusterInvoker;
import com.saucesubfresh.rpc.client.cluster.support.FailoverClusterInvoker;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.loadbalance.support.ConsistentHashLoadBalance;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.client.random.support.SequenceRequestIdGenerator;
import com.saucesubfresh.rpc.client.remoting.*;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.client.store.support.MemoryInstanceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijunping on 2022/1/20
 */
@Configuration
@EnableConfigurationProperties(ClientConfiguration.class)
public class ClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ServiceDiscovery.class)
    public ClusterInvoker clusterInvoker(ServiceDiscovery serviceDiscovery,
                                         ClientConfiguration configuration,
                                         LoadBalance loadBalance,
                                         RemotingInvoker remotingInvoker){
        return new FailoverClusterInvoker(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalance loadBalance(){
        return new ConsistentHashLoadBalance();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestIdGenerator randomGenerator(){
        return new SequenceRequestIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public InstanceStore memoryStore(){
        return new MemoryInstanceStore();
    }

    @Bean
    public NettyClientHandler clientHandler(){
        return new NettyClientHandler();
    }

    @Bean
    public NettyChannelInitializer channelInitializer(NettyClientHandler clientHandler){
        return new NettyChannelInitializer(clientHandler);
    }

    @Bean
    public NettyClient nettyClient(NettyChannelInitializer channelInitializer){
        return new NettyClient(channelInitializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingInvoker remotingInvoker(NettyClient nettyClient, RequestIdGenerator requestIdGenerator){
        return new NettyRemotingInvoker(nettyClient, requestIdGenerator);
    }
}
