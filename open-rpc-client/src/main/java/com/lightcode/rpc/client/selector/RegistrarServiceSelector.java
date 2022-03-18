package com.lightcode.rpc.client.selector;

import com.lightcode.rpc.client.annotation.EnableLightRpcClient;
import com.lightcode.rpc.client.registry.support.NacosRegistryService;
import com.lightcode.rpc.client.registry.support.ZookeeperRegistryService;
import com.lightcode.rpc.core.enums.RegistryServiceType;
import com.lightcode.rpc.core.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author lijunping on 2022/1/20
 */
@Slf4j
public class RegistrarServiceSelector implements ImportSelector {

    /**
     * The name of {@link RegistryServiceType} attributes in {@link EnableLightRpcClient}
     */
    private static final String REGISTRAR_TYPE_ATTRIBUTE_NAME = "registryType";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes =
                importingClassMetadata.getAnnotationAttributes(EnableLightRpcClient.class.getName());
        RegistryServiceType serverServiceType = (RegistryServiceType) annotationAttributes.get(REGISTRAR_TYPE_ATTRIBUTE_NAME);
        log.info("Use the [{}] method to register the Client service", serverServiceType);
        switch (serverServiceType) {
            case NACOS:
                return new String[]{NacosRegistryService.class.getName()};
            case ZOOKEEPER:
                return new String[]{ZookeeperRegistryService.class.getName()};
        }
        throw new RpcException("Unsupported ServerServiceType：" + serverServiceType);
    }
}
