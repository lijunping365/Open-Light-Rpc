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
@ConditionalOnBean(EnableOpenRpcServer.class)
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
    public GrpcMessageHandler gRpcMessageHandler(RegistryService registryService,
                                                 MessageProcess messageProcess,
                                                 ServerConfiguration configuration){
        return new GrpcMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    public GrpcServer grpcServer(ServerConfiguration configuration,
                                 GrpcMessageHandler bindableService){
        return new GrpcServer(configuration, bindableService);
    }
}
