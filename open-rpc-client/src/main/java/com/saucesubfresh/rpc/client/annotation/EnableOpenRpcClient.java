package com.saucesubfresh.rpc.client.annotation;

import com.saucesubfresh.rpc.core.enums.RegistryServiceType;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.discovery.support.NacosRegistryService;
import com.saucesubfresh.rpc.client.discovery.support.ZookeeperRegistryService;
import com.saucesubfresh.rpc.client.selector.RegistryServiceSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lijunping on 2022/1/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RegistryServiceSelector.class})
public @interface EnableOpenRpcClient {

    /**
     * Choose how to register to Server
     *
     * @return {@link RegistryServiceType} instance
     * @see ServiceDiscovery
     * @see NacosRegistryService
     * @see ZookeeperRegistryService
     */
    RegistryServiceType registryType() default RegistryServiceType.NACOS;
}
