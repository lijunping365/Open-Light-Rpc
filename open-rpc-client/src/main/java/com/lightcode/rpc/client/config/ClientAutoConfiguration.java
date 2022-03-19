package com.lightcode.rpc.client.config;

import com.lightcode.rpc.client.ClientConfiguration;
import com.lightcode.rpc.client.process.DefaultMessageProcess;
import com.lightcode.rpc.client.process.MessageProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * NamingService 和 zkClient 客户端和服务端的配置要保持一致
 * @author lijunping on 2022/1/20
 */
@Configuration
@EnableConfigurationProperties(ClientConfiguration.class)
public class ClientAutoConfiguration {

    private final ClientConfiguration configuration;

    public ClientAutoConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageProcess messageProcess(){
        return new DefaultMessageProcess();
    }
}
