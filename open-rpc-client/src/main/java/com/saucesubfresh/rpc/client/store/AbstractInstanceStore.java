package com.saucesubfresh.rpc.client.store;

import com.saucesubfresh.rpc.core.enums.Status;
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
        if (CollectionUtils.isEmpty(instances)){
            return;
        }
        List<ServerInformation> serverInstances = handler(instances);
        serverInstances.forEach(e-> put(e.getServerId(), e));
    }

    /**
     * handler online servers
     *
     * @param onLineServers online servers
     * @return 返回 online servers  + offline servers
     */
    protected List<ServerInformation> handler(List<ServerInformation> onLineServers){
        long currentTime = System.currentTimeMillis();
        onLineServers.forEach(instance->{
            instance.setStatus(Status.ON_LINE);
            instance.setOnlineTime(currentTime);
        });

        List<ServerInformation> cacheServers = getAll();
        List<String> onlineServerIds = onLineServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        List<String> cacheServerIds = cacheServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        cacheServerIds.removeAll(onlineServerIds);
        List<ServerInformation> offLineClients = cacheServers.stream().filter(e -> cacheServerIds.contains(e.getServerId())).collect(Collectors.toList());
        offLineClients.forEach(instance->{
            instance.setStatus(Status.OFF_LINE);
            instance.setOnlineTime(currentTime);
        });
        onLineServers.addAll(offLineClients);
        return onLineServers;
    }

    protected abstract void put(String serverId, ServerInformation instance);
}
