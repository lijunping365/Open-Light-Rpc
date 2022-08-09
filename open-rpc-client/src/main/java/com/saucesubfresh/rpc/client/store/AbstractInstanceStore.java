package com.saucesubfresh.rpc.client.store;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/18
 */
public abstract class AbstractInstanceStore implements InstanceStore{

    @Override
    public void put(List<ClientInformation> instances) {
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
    private void handlerOnline(List<ClientInformation> instances, long currentTime){
        instances.forEach(instance->{
            final String clientId = instance.getClientId();
            ClientInformation clientInformation = get(clientId);
            if (Objects.isNull(clientInformation) || clientInformation.getStatus() == ClientStatus.OFF_LINE){
                instance.setStatus(ClientStatus.ON_LINE);
                instance.setOnlineTime(currentTime);
                put(clientId, instance);
            }
        });
    }


    /**
     * 处理下线的服务端
     * @param instances 上线的服务端列表
     */
    private void handlerOffline(List<ClientInformation> instances, long currentTime){
        List<ClientInformation> cacheClients = getAll();
        if (CollectionUtils.isEmpty(cacheClients)){
            return;
        }

        List<String> onlineClientIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)){
            onlineClientIds = instances.stream().map(ClientInformation::getClientId).collect(Collectors.toList());
        }

        List<String> cacheClientIds = cacheClients.stream().map(ClientInformation::getClientId).collect(Collectors.toList());
        cacheClientIds.removeAll(onlineClientIds);
        cacheClientIds.forEach(clientId-> {
            ClientInformation instance = get(clientId);
            instance.setStatus(ClientStatus.OFF_LINE);
            instance.setOnlineTime(currentTime);
            put(instance.getClientId(), instance);
        });
    }

    @Override
    public List<ClientInformation> getOnlineList() {
        List<ClientInformation> clients = getAll();
        if (CollectionUtils.isEmpty(clients)){
            return Collections.emptyList();
        }
        return clients.stream().filter(e->e.getStatus() == ClientStatus.ON_LINE).collect(Collectors.toList());
    }

    protected abstract ClientInformation get(String clientId);

    protected abstract void put(String clientId, ClientInformation instance);
}
