package com.saucesubfresh.rpc.client.discovery;



import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * @author lijunping on 2022/1/20
 */
public interface ServiceDiscovery {

    /**
     * 查找在线服务端列表
     * @return
     */
    List<ServerInformation> lookup();

    /**
     * 通知服务端下线
     * @param clientId
     * @return
     */
    boolean offlineClient(String clientId);

    /**
     * 通知服务端上线
     * @param clientId
     * @return
     */
    boolean onlineClient(String clientId);

}
