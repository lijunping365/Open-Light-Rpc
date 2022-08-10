package com.saucesubfresh.rpc.server.config;

import com.saucesubfresh.rpc.server.ServerConfiguration;
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
@Configuration
@EnableConfigurationProperties(ServerConfiguration.class)
public class ServerAutoConfiguration {

//    @Bean
//    @ConditionalOnBean(RegistryService.class)
//    public GrpcMessageHandler gRpcMessageHandler(RegistryService registryService,
//                                                 MessageProcess messageProcess,
//                                                 ServerConfiguration configuration){
//        return new GrpcMessageHandler(messageProcess, configuration, registryService);
//    }
//
//    @Bean
//    @ConditionalOnBean(GrpcMessageHandler.class)
//    public GrpcServer grpcClient(ServerConfiguration configuration,
//                                 GrpcMessageHandler bindableService){
//        return new GrpcServer(configuration, bindableService);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public MessageProcess messageProcess(){
//        return new DefaultMessageProcess();
//    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }

    @Bean
    @ConditionalOnBean(RegistryService.class)
    public NettyMessageHandler nettyMessageHandler(RegistryService registryService,
                                                   MessageProcess messageProcess,
                                                   ServerConfiguration configuration){
        return new NettyMessageHandler(messageProcess, configuration, registryService);
    }

    @Bean
    public NettyChannelInitializer channelInitializer(NettyMessageHandler nettyMessageHandler){
        return new NettyChannelInitializer(nettyMessageHandler);
    }

    @Bean
    public NettyServer nettyServer(ServerConfiguration configuration,
                                   NettyChannelInitializer channelInitializer){
        return new NettyServer(configuration, channelInitializer);
    }
}
