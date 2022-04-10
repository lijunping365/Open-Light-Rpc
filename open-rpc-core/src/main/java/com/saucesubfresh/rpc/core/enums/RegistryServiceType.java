package com.saucesubfresh.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lijunping on 2022/1/20
 */
@Getter
@AllArgsConstructor
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
