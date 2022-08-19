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
public class LocalInstanceStore extends AbstractInstanceStore {

    private final Map<String, List<ServerInformation>> store = PlatformDependent.newConcurrentHashMap();

    @Override
    public void put(String namespace, List<ServerInformation> instances) {
        store.put(namespace, super.handler(namespace, instances));
    }

    @Override
    public List<ServerInformation> getByNamespace(String namespace) {
        return store.getOrDefault(namespace, new ArrayList<>());
    }
}
