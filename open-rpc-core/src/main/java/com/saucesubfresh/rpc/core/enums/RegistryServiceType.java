package com.saucesubfresh.rpc.core.enums;

import lombok.Getter;

/**
 * Corresponding to the type definition
 *
 * @author lijunping on 2022/1/20
 */
@Getter
public enum RegistryServiceType {
    /**
     * Use nacos client register to server
     */
    NACOS,
    /**
     * Use zookeeper client register to server
     */
    ZOOKEEPER,
    ;
}
