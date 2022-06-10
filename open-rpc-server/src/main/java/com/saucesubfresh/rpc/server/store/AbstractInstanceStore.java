package com.saucesubfresh.rpc.server.store;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/18
 */
public abstract class AbstractInstanceStore implements InstanceStore{

    protected void buildClient(List<ClientInformation> instances, ClientStatus clientStatus, long currentTime){
        if (CollectionUtils.isEmpty(instances)){
            return;
        }
        instances.forEach(instance->{
            instance.setStatus(clientStatus);
            instance.setOnlineTime(currentTime);
        });
    }

    protected List<ClientInformation> getClient(String namespace, List<ClientInformation> onLineClients){
        long currentTime = new Date().getTime();

        List<String> onlineClientIds = onLineClients.stream().map(ClientInformation::getClientId).collect(Collectors.toList());
        buildClient(onLineClients, ClientStatus.ON_LINE, currentTime);

        List<ClientInformation> cacheClients = getByNamespace(namespace);
        List<String> cacheClientIds = cacheClients.stream().map(ClientInformation::getClientId).collect(Collectors.toList());
        cacheClientIds.removeAll(onlineClientIds);
        List<ClientInformation> offLineClients = cacheClients.stream().filter(e -> cacheClientIds.contains(e.getClientId())).collect(Collectors.toList());
        buildClient(offLineClients, ClientStatus.OFF_LINE, currentTime);
        onLineClients.addAll(cacheClients);
        return onLineClients;
    }
}
