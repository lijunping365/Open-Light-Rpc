package com.lightcode.rpc.client.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.lightcode.rpc.client.ClientConfiguration;
import com.lightcode.rpc.client.registry.support.NacosRegistryService;
import com.lightcode.rpc.client.registry.support.ZookeeperRegistryService;
import com.lightcode.rpc.core.constants.CommonConstant;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

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
    @ConditionalOnBean(NacosRegistryService.class)
    @ConditionalOnMissingBean
    public NamingService namingService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.USERNAME, configuration.getUsername());
        properties.put(PropertyKeyConst.PASSWORD, configuration.getPassword());
        properties.put(PropertyKeyConst.SERVER_ADDR, String.format(CommonConstant.ADDRESS_PATTERN, configuration.getAddress(), configuration.getPort()));
        return NacosFactory.createNamingService(properties);
    }

    @Bean
    @ConditionalOnBean(ZookeeperRegistryService.class)
    @ConditionalOnMissingBean
    public ZkClient zkClient(){
        System.setProperty("zookeeper.sasl.client", "false");
        return new ZkClient(String.format(CommonConstant.ADDRESS_PATTERN, configuration.getAddress(), configuration.getPort()), configuration.getConnectionTimeout());
    }

}
