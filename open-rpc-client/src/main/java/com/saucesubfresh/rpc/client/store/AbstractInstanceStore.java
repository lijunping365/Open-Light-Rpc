package com.saucesubfresh.rpc.client.store;

import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/18
 */
public abstract class AbstractInstanceStore implements InstanceStore{

    /**
     * handler online servers
     *
     * @param onLineServers online servers
     * @return 返回 online servers  + offline servers
     */
    protected List<ServerInformation> handler(String namespace, List<ServerInformation> onLineServers){
        List<ServerInformation> cacheServers = getByNamespace(namespace);
        List<String> onlineServerIds = onLineServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        List<String> cacheServerIds = cacheServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        cacheServerIds.removeAll(onlineServerIds);
        List<ServerInformation> offLineClients = cacheServers.stream().filter(e -> cacheServerIds.contains(e.getServerId())).collect(Collectors.toList());
        long currentTime = System.currentTimeMillis();
        offLineClients.forEach(instance->{
            instance.setStatus(Status.OFF_LINE);
            instance.setOnlineTime(currentTime);
        });
        onLineServers.addAll(offLineClients);
        return onLineServers;
    }
}
