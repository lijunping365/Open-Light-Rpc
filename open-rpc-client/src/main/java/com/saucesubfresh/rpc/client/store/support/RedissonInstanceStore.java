package com.saucesubfresh.rpc.client.store.support;

import com.saucesubfresh.rpc.client.store.AbstractInstanceStore;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class RedissonInstanceStore extends AbstractInstanceStore {

    private static final String KEY = "instance:store";

    private final Map<String, List<ServerInformation>> map;

    public RedissonInstanceStore(RedissonClient redisson){
        this.map = redisson.getMap(KEY);
    }

    @Override
    public void put(String namespace, List<ServerInformation> instances) {
        map.put(namespace, super.handler(namespace, instances));
    }

    @Override
    public List<ServerInformation> getByNamespace(String namespace) {
        return map.getOrDefault(namespace, new ArrayList<>());
    }
}
