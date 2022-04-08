package com.saucesubfresh.rpc.server.config;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.cluster.ClusterInvoker;
import com.saucesubfresh.rpc.server.cluster.support.FailoverClusterInvoker;
import com.saucesubfresh.rpc.server.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.server.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.server.loadbalance.support.ConsistentHashLoadBalance;
import com.saucesubfresh.rpc.server.random.RequestIdGenerator;
import com.saucesubfresh.rpc.server.random.support.SequenceRequestIdGenerator;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.server.remoting.GrpcRemotingInvoker;
import com.saucesubfresh.rpc.server.remoting.GrpcServer;
import com.saucesubfresh.rpc.server.store.InstanceStore;
import com.saucesubfresh.rpc.server.store.support.MemoryInstanceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijunping on 2022/1/20
 */
@Configuration
@EnableConfigurationProperties(ServerConfiguration.class)
public class ServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ServiceDiscovery.class)
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
    public RemotingInvoker remotingInvoker(RequestIdGenerator requestIdGenerator){
        return new GrpcRemotingInvoker(requestIdGenerator);
    }

    @Bean
    public GrpcServer grpcServer(ServerConfiguration configuration){
        return new GrpcServer(configuration);
    }
}
