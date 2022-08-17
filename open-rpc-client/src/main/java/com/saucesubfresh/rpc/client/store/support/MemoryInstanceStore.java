package com.saucesubfresh.rpc.client.store.support;

import com.saucesubfresh.rpc.client.store.AbstractInstanceStore;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class MemoryInstanceStore extends AbstractInstanceStore {

    private final Map<String, ServerInformation> store = PlatformDependent.newConcurrentHashMap();

    @Override
    public void put(String serverId, ServerInformation instance) {
        store.put(serverId, instance);
    }

    @Override
    public List<ServerInformation> getAll() {
        return new ArrayList<>(store.values());
    }
}
