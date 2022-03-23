package com.lightcode.rpc.server.config;

import com.lightcode.rpc.server.ServerConfiguration;
import com.lightcode.rpc.server.cluster.ClusterInvoker;
import com.lightcode.rpc.server.cluster.support.FailoverClusterInvoker;
import com.lightcode.rpc.server.discovery.ServiceDiscovery;
import com.lightcode.rpc.server.loadbalance.LoadBalance;
import com.lightcode.rpc.server.loadbalance.support.ConsistentHashLoadBalance;
import com.lightcode.rpc.server.random.RequestIdGenerator;
import com.lightcode.rpc.server.random.support.SequenceRequestIdGenerator;
import com.lightcode.rpc.server.remoting.RemotingInvoker;
import com.lightcode.rpc.server.remoting.support.GrpcRemotingInvoker;
import com.lightcode.rpc.server.remoting.support.GrpcServer;
import com.lightcode.rpc.server.store.InstanceStore;
import com.lightcode.rpc.server.store.support.MemoryInstanceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * NamingService 和 zkClient 客户端和服务端的配置要保持一致
 * @author lijunping on 2022/1/20
 */
@Configuration
@EnableConfigurationProperties(ServerConfiguration.class)
public class ServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClusterInvoker clusterInvoker(ServiceDiscovery serviceDiscovery,
                                         ServerConfiguration configuration,
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
    public RemotingInvoker remotingInvoker(RequestIdGenerator requestIdGenerator){
        return new GrpcRemotingInvoker(requestIdGenerator);
    }

    @Bean
    public GrpcServer grpcServer(ServerConfiguration configuration){
        return new GrpcServer(configuration);
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
}
