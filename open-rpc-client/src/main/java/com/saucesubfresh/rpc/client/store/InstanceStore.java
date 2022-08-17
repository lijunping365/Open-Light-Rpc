package com.saucesubfresh.rpc.client.store;



import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    default List<ServerInformation> getOnlineList(){
        List<ServerInformation> serverInstances = getAll();
        if (CollectionUtils.isEmpty(serverInstances)){
            return Collections.emptyList();
        }
        return serverInstances.stream().filter(e->e.getStatus() == Status.ON_LINE).collect(Collectors.toList());
    }
}
