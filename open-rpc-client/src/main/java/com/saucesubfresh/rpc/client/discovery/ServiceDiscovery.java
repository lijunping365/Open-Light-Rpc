package com.saucesubfresh.rpc.client.discovery;


import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * 服务发现
 * @author lijunping on 2022/1/20
 */
public interface ServiceDiscovery {

    /**
     * 查找在线服务端列表
     * @return
     */
    List<ServerInformation> lookup();
}
