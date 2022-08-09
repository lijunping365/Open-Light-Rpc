package com.saucesubfresh.rpc.server.annotation;

import com.saucesubfresh.rpc.server.registry.RegistryService;
import com.saucesubfresh.rpc.server.registry.support.NacosRegistryService;
import com.saucesubfresh.rpc.server.registry.support.ZookeeperRegistryService;
import com.saucesubfresh.rpc.server.selector.RegistrarServiceSelector;
import com.saucesubfresh.rpc.core.enums.RegistryServiceType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lijunping on 2022/1/20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RegistrarServiceSelector.class})
public @interface EnableOpenRpcServer {

    /**
     * Choose how to register to Server
     *
     * @return {@link RegistryServiceType} instance
     * @see RegistryService
     * @see NacosRegistryService
     * @see ZookeeperRegistryService
     */
    RegistryServiceType registryType() default RegistryServiceType.NACOS;
}
