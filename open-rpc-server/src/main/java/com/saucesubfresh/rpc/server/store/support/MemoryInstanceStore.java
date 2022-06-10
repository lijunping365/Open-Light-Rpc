package com.saucesubfresh.rpc.server.store.support;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.store.AbstractInstanceStore;
import io.netty.util.internal.PlatformDependent;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/17
 */
public class MemoryInstanceStore extends AbstractInstanceStore {

    private final Map<String, List<ClientInformation>> store = PlatformDependent.newConcurrentHashMap(16);

    @Override
    public void put(String namespace, List<ClientInformation> instances) {
        List<ClientInformation> cacheClients = getByNamespace(namespace);
        List<String> cacheClientIds = cacheClients.stream().map(ClientInformation::getClientId).collect(Collectors.toList());
        List<String> onlineClientIds = instances.stream().map(ClientInformation::getClientId).collect(Collectors.toList());

        List<ClientInformation> clients = new ArrayList<>();

        long currentTime = new Date().getTime();
        buildClient(instances, ClientStatus.ON_LINE, currentTime);
        cacheClientIds.removeAll(onlineClientIds);
        buildClient(cacheClients, ClientStatus.OFF_LINE, currentTime);

        // 1 所有上线的 client
        clients.addAll(instances);
        // 2 所有下线的 client
        clients.addAll(cacheClients);
        // 3 上线+下线
        store.put(namespace, clients);
    }

    @Override
    public List<ClientInformation> getByNamespace(String namespace) {
        final List<ClientInformation> clients = store.get(namespace);
        if (CollectionUtils.isEmpty(clients)){
            return Collections.emptyList();
        }
        return clients;
    }
}
