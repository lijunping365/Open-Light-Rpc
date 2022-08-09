package com.saucesubfresh.rpc.client.store.support;

import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.client.store.AbstractInstanceStore;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class MemoryInstanceStore extends AbstractInstanceStore {

    private final Map<String, ClientInformation> store = PlatformDependent.newConcurrentHashMap();

    @Override
    protected ClientInformation get(String clientId) {
        return store.get(clientId);
    }

    @Override
    protected void put(String key, ClientInformation instance) {
        store.put(key, instance);
    }

    @Override
    public List<ClientInformation> getAll() {
        return new ArrayList<>(store.values());
    }
}
