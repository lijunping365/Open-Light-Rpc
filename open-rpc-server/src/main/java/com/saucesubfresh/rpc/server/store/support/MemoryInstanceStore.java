package com.saucesubfresh.rpc.server.store.support;

import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.store.AbstractInstanceStore;
import io.netty.util.internal.PlatformDependent;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class MemoryInstanceStore extends AbstractInstanceStore {

    private final Map<String, List<ClientInformation>> store = PlatformDependent.newConcurrentHashMap(16);

    @Override
    public void put(String namespace, List<ClientInformation> onLineClients) {
        final List<ClientInformation> client = getClient(namespace, onLineClients);
        store.put(namespace, client);
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
