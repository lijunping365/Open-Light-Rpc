package com.saucesubfresh.rpc.client.store;



import com.saucesubfresh.rpc.core.information.ClientInformation;

import java.util.List;

/**
 * 服务端实例缓存
 * @author lijunping on 2022/2/17
 */
public interface InstanceStore {

    /**
     * @param instances 要上线的服务端列表
     */
    void put(List<ClientInformation> instances);

    /**
     * @return 返回缓存中全部的服务端列表
     */
    List<ClientInformation> getAll();

    /**
     * @return 返回缓存中在线的服务端列表
     */
    List<ClientInformation> getOnlineList();
}
