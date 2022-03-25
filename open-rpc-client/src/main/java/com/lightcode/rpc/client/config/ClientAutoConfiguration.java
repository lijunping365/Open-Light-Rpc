package com.lightcode.rpc.client.config;

import com.lightcode.rpc.client.ClientConfiguration;
import com.lightcode.rpc.client.remoting.GrpcMessageHandler;
import com.lightcode.rpc.client.process.DefaultMessageProcess;
import com.lightcode.rpc.client.process.MessageProcess;
import com.lightcode.rpc.client.registry.RegistryService;
import com.lightcode.rpc.client.remoting.GrpcClient;
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
    public GrpcMessageHandler gRpcMessageHandler(RegistryService registryService,
                                                 MessageProcess messageProcess,
                                                 ClientConfiguration configuration){
        return new GrpcMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    public GrpcClient grpcClient(ClientConfiguration configuration,
                                 GrpcMessageHandler bindableService){
        return new GrpcClient(configuration, bindableService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }
}
