package com.lightcode.rpc.server.config;

import com.lightcode.rpc.server.ServerConfiguration;
import com.lightcode.rpc.server.random.RandomGenerator;
import com.lightcode.rpc.server.random.support.SnowflakeRandomGenerator;
import com.lightcode.rpc.server.remoting.RemotingInvoker;
import com.lightcode.rpc.server.remoting.support.GrpcRemotingInvoker;
import com.lightcode.rpc.server.store.InstanceStore;
import com.lightcode.rpc.server.store.support.MemoryInstanceStore;
import com.lightcode.rpc.server.store.support.RedissonInstanceStore;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * NamingService 和 zkClient 客户端和服务端的配置要保持一致
 * @author lijunping on 2022/1/20
 */
@Configuration
@EnableConfigurationProperties(ServerConfiguration.class)
public class ServerAutoConfiguration {

    private final ServerConfiguration configuration;

    public ServerAutoConfiguration(ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingInvoker remotingInvoker(RandomGenerator randomGenerator){
        return new GrpcRemotingInvoker(randomGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public RandomGenerator randomGenerator(){
        return new SnowflakeRandomGenerator(ThreadLocalRandom.current().nextInt(1, 30), ThreadLocalRandom.current().nextInt(1, 30));
    }

    @Bean
    @ConditionalOnExpression("'memory'.equals('${com.lightcode.rpc.server.store:memory}')")
    public InstanceStore memoryStore(){
        return new MemoryInstanceStore();
    }

    @Bean
    @ConditionalOnExpression("'redisson'.equals('${com.lightcode.rpc.server.store:redisson}')")
    public InstanceStore redissonStore(RedissonClient client){
        return new RedissonInstanceStore(client);
    }
}
