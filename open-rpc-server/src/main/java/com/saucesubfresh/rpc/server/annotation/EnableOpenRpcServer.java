package com.saucesubfresh.rpc.server.annotation;

import com.saucesubfresh.rpc.core.enums.RegistryServiceType;
import com.saucesubfresh.rpc.server.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.server.discovery.support.NacosRegistryService;
import com.saucesubfresh.rpc.server.discovery.support.ZookeeperRegistryService;
import com.saucesubfresh.rpc.server.selector.RegistryServiceSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RegistryServiceSelector.class})
public @interface EnableOpenRpcServer {

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
