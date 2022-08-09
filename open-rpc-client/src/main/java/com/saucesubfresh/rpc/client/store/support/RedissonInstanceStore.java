package com.saucesubfresh.rpc.client.store.support;

import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.client.store.AbstractInstanceStore;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class RedissonInstanceStore extends AbstractInstanceStore {

    private static final String KEY = "instance:store";

    private final Map<String, ServerInformation> map;

    public RedissonInstanceStore(RedissonClient redisson){
        this.map = redisson.getMap(KEY);
    }

    @Override
    protected ServerInformation get(String clientId) {
        return map.get(clientId);
    }

    @Override
    protected void put(String clientId, ServerInformation instance) {
        map.put(clientId, instance);
    }

    @Override
    public List<ServerInformation> getAll() {
        return new ArrayList<>(map.values());
    }
}
