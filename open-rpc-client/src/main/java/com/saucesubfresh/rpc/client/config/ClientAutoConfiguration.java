package com.saucesubfresh.rpc.client.config;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.random.RequestIdGenerator;
import com.saucesubfresh.rpc.client.random.support.SequenceRequestIdGenerator;
import com.saucesubfresh.rpc.client.remoting.GrpcMessageHandler;
import com.saucesubfresh.rpc.client.process.DefaultMessageProcess;
import com.saucesubfresh.rpc.client.process.MessageProcess;
import com.saucesubfresh.rpc.client.registry.RegistryService;
import com.saucesubfresh.rpc.client.remoting.GrpcClient;
import com.saucesubfresh.rpc.client.remoting.GrpcRemotingInvoker;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
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
    @ConditionalOnBean(RegistryService.class)
    public GrpcMessageHandler gRpcMessageHandler(RegistryService registryService,
                                                 MessageProcess messageProcess,
                                                 ClientConfiguration configuration){
        return new GrpcMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    @ConditionalOnBean(GrpcMessageHandler.class)
    public GrpcClient grpcClient(ClientConfiguration configuration,
                                 GrpcMessageHandler bindableService){
        return new GrpcClient(configuration, bindableService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestIdGenerator randomGenerator(){
        return new SequenceRequestIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingInvoker remotingInvoker(RequestIdGenerator requestIdGenerator){
        return new GrpcRemotingInvoker(requestIdGenerator);
    }
}
