package com.saucesubfresh.rpc.server.config;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.remoting.GrpcMessageHandler;
import com.saucesubfresh.rpc.server.process.DefaultMessageProcess;
import com.saucesubfresh.rpc.server.process.MessageProcess;
import com.saucesubfresh.rpc.server.registry.RegistryService;
import com.saucesubfresh.rpc.server.remoting.GrpcClient;
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
public class ClientAutoConfiguration {

    @Bean
    @ConditionalOnBean(RegistryService.class)
    public GrpcMessageHandler gRpcMessageHandler(RegistryService registryService,
                                                 MessageProcess messageProcess,
                                                 ServerConfiguration configuration){
        return new GrpcMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    @ConditionalOnBean(GrpcMessageHandler.class)
    public GrpcClient grpcClient(ServerConfiguration configuration,
                                 GrpcMessageHandler bindableService){
        return new GrpcClient(configuration, bindableService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }
}
