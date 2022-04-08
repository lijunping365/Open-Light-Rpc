package com.saucesubfresh.rpc.server.discovery;



import com.lightcode.rpc.core.information.ClientInformation;

import java.util.List;

/**
 * @author lijunping on 2022/1/20
 */
public interface ServiceDiscovery {

    /**
     * 查找在线客户端列表
     * @return
     */
    List<ClientInformation> lookup();

    /**
     * 通知客户端下线
     * @param clientId
     * @return
     */
    boolean offlineClient(String clientId);

    /**
     * 通知客户端上线
     * @param clientId
     * @return
     */
    boolean onlineClient(String clientId);

}
