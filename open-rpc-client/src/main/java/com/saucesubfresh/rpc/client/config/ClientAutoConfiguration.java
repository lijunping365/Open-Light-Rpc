package com.saucesubfresh.rpc.client.config;

import com.alibaba.nacos.api.naming.NamingService;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.annotation.EnableOpenRpcClient;
import com.saucesubfresh.rpc.client.cluster.ClusterInvoker;
import com.saucesubfresh.rpc.client.cluster.support.FailoverClusterInvoker;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.discovery.support.NacosRegistryService;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.loadbalance.support.ConsistentHashLoadBalance;
import com.saucesubfresh.rpc.client.manager.DefaultInstanceManager;
import com.saucesubfresh.rpc.client.manager.InstanceManager;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.client.random.support.SequenceRequestIdGenerator;
import com.saucesubfresh.rpc.client.remoting.GrpcClient;
import com.saucesubfresh.rpc.client.remoting.GrpcRemotingInvoker;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.client.remoting.RpcClient;
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
    public RpcClient client(){
        return new GrpcClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingInvoker remotingInvoker(RpcClient client, RequestIdGenerator requestIdGenerator){
        return new GrpcRemotingInvoker(client, requestIdGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public InstanceManager instanceManager(RemotingInvoker remotingInvoker){
        return new DefaultInstanceManager(remotingInvoker);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(NamingService.class)
    public ServiceDiscovery serviceDiscovery(NamingService namingService,
                                             InstanceStore instanceStore,
                                             ClientConfiguration configuration){
        return new NacosRegistryService(namingService, instanceStore, configuration);
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
