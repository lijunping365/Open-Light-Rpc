package com.saucesubfresh.rpc.client.store;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/18
 */
public abstract class AbstractInstanceStore implements InstanceStore{

    @Override
    public void put(List<ServerInformation> instances) {
        long currentTime = new Date().getTime();
        handlerOffline(instances, currentTime);
        if (!CollectionUtils.isEmpty(instances)){
            handlerOnline(instances, currentTime);
        }
    }

    /**
     * 处理服务端的上线
     * @param instances 上线的服务端列表
     */
    private void handlerOnline(List<ServerInformation> instances, long currentTime){
        instances.forEach(instance->{
            final String serverId = instance.getServerId();
            ServerInformation serverInformation = get(serverId);
            if (Objects.isNull(serverInformation) || serverInformation.getStatus() == ClientStatus.OFF_LINE){
                instance.setStatus(ClientStatus.ON_LINE);
                instance.setOnlineTime(currentTime);
                put(serverId, instance);
            }
        });
    }


    /**
     * 处理下线的服务端
     * @param instances 上线的服务端列表
     */
    private void handlerOffline(List<ServerInformation> instances, long currentTime){
        List<ServerInformation> cacheClients = getAll();
        if (CollectionUtils.isEmpty(cacheClients)){
            return;
        }

        List<String> onlineServerIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)){
            onlineServerIds = instances.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        }

        List<String> cacheServerIds = cacheClients.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        cacheServerIds.removeAll(onlineServerIds);
        cacheServerIds.forEach(serverId-> {
            ServerInformation instance = get(serverId);
            instance.setStatus(ClientStatus.OFF_LINE);
            instance.setOnlineTime(currentTime);
            put(instance.getServerId(), instance);
        });
    }

    @Override
    public List<ServerInformation> getOnlineList() {
        List<ServerInformation> clients = getAll();
        if (CollectionUtils.isEmpty(clients)){
            return Collections.emptyList();
        }
        return clients.stream().filter(e->e.getStatus() == ClientStatus.ON_LINE).collect(Collectors.toList());
    }

    protected abstract ServerInformation get(String serverId);

    protected abstract void put(String serverId, ServerInformation instance);
}
