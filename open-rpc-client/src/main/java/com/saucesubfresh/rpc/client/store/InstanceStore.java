package com.saucesubfresh.rpc.client.store;



import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * 服务端实例缓存
 * @author lijunping on 2022/2/17
 */
public interface InstanceStore {

    /**
     * @param instances 要上线的服务端列表
     */
    void put(List<ServerInformation> instances);

    /**
     * @return 返回缓存中全部的服务端列表
     */
    List<ServerInformation> getAll();

    /**
     * @return 返回缓存中在线的服务端列表
     */
    List<ServerInformation> getOnlineList();
}
