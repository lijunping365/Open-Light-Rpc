package com.saucesubfresh.rpc.server.store;



import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端实例缓存
 * @author lijunping on 2022/2/17
 */
public interface InstanceStore {

    /**
     * @param namespace 应用名称
     * @param instances 要上线的客户端列表
     */
    void put(String namespace, List<ClientInformation> instances);

    /**
     * @return 返回缓存中全部的客户端列表
     */
    List<ClientInformation> getByNamespace(String namespace);

    /**
     * @return 返回缓存中在线的客户端列表
     */
    default List<ClientInformation> getOnlineList(String namespace){
        List<ClientInformation> clients = getByNamespace(namespace);
        if (CollectionUtils.isEmpty(clients)){
            return Collections.emptyList();
        }
        return clients.stream().filter(e->e.getStatus() == ClientStatus.ON_LINE).collect(Collectors.toList());
    }
}
