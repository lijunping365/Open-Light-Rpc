package com.saucesubfresh.rpc.server.manager;

/**
 * @author lijunping on 2022/6/10
 */
public interface InstanceManager {

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
