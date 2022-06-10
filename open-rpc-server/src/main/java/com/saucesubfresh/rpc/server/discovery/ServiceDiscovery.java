package com.saucesubfresh.rpc.server.discovery;

import com.saucesubfresh.rpc.core.information.ClientInformation;

import java.util.List;

/**
 * @author lijunping on 2022/1/20
 */
public interface ServiceDiscovery {

    /**
     * 查找在线客户端列表
     * @param namespace
     * @return
     */
    List<ClientInformation> lookup(String namespace);
}
