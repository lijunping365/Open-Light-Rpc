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
        buildServer(onLineServers, Status.ON_LINE, currentTime);

        List<ServerInformation> cacheServers = getAll();
        List<String> onlineServerIds = onLineServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        List<String> cacheServerIds = cacheServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        cacheServerIds.removeAll(onlineServerIds);
        List<ServerInformation> offLineClients = cacheServers.stream().filter(e -> cacheServerIds.contains(e.getServerId())).collect(Collectors.toList());

        buildServer(offLineClients, Status.OFF_LINE, currentTime);
        onLineServers.addAll(offLineClients);
        return onLineServers;
    }

    protected void buildServer(List<ServerInformation> instances, Status serverStatus, long currentTime){
        if (CollectionUtils.isEmpty(instances)){
            return;
        }
        instances.forEach(instance->{
            instance.setStatus(serverStatus);
            instance.setOnlineTime(currentTime);
        });
    }

    protected abstract void put(String serverId, ServerInformation instance);
}
