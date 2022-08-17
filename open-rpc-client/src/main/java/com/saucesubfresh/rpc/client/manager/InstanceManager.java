package com.saucesubfresh.rpc.client.manager;

/**
 * 管理服务
 * 
 * @author lijunping on 2022/8/17
 */
public interface InstanceManager {

    /**
     * 通知服务端下线
     * @param serverId
     * @return
     */
    boolean offlineServer(String serverId);

    /**
     * 通知服务端上线
     * @param serverId
     * @return
     */
    boolean onlineServer(String serverId);
}
