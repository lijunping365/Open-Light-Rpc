package com.saucesubfresh.rpc.server.store.support;

import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.store.AbstractInstanceStore;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class RedissonInstanceStore extends AbstractInstanceStore {

    private static final String KEY = "instance:store";

    private final Map<String, List<ClientInformation>> map;

    public RedissonInstanceStore(RedissonClient redisson){
        this.map = redisson.getMap(KEY);
    }

    @Override
    public void put(String namespace, List<ClientInformation> instances) {
        map.put(namespace, instances);
    }

    @Override
    public List<ClientInformation> getByNamespace(String namespace) {
        return map.get(namespace);
    }
}
