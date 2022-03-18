package com.lightcode.rpc.server.annotation;


import com.lightcode.rpc.core.enums.RegistryServiceType;
import com.lightcode.rpc.server.selector.RegistryServiceSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RegistryServiceSelector.class})
public @interface EnableLightRpcServer {

    /**
     * Configure the way to pull the client
     *
     * @return {@link RegistryServiceType} instance
     */
    RegistryServiceType registryType() default RegistryServiceType.NACOS;
}
