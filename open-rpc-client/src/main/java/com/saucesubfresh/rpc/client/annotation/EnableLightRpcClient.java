package com.saucesubfresh.rpc.client.annotation;

import com.saucesubfresh.rpc.client.selector.RegistrarServiceSelector;
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
public @interface EnableLightRpcClient {

    RegistryServiceType registryType() default RegistryServiceType.NACOS;
}
