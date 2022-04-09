package com.saucesubfresh.rpc.server.store;



import com.saucesubfresh.rpc.core.information.ClientInformation;

import java.util.List;

/**
 * 客户端实例缓存
 * @author lijunping on 2022/2/17
 */
public interface InstanceStore {

    /**
     * @param instances 要上线的客户端列表
     */
    void put(List<ClientInformation> instances);

    /**
     * @return 返回缓存中全部的客户端列表
     */
    List<ClientInformation> getAll();

    /**
     * @return 返回缓存中在线的客户端列表
     */
    List<ClientInformation> getOnlineList();
}
