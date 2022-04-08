package com.saucesubfresh.rpc.server.store.support;

import com.lightcode.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.store.AbstractInstanceStore;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class RedissonInstanceStore extends AbstractInstanceStore {

    private static final String KEY = "instance:store";

    private final Map<String, ClientInformation> map;

    public RedissonInstanceStore(RedissonClient redisson){
        this.map = redisson.getMap(KEY);
    }

    @Override
    protected ClientInformation get(String clientId) {
        return map.get(clientId);
    }

    @Override
    protected void put(String clientId, ClientInformation instance) {
        map.put(clientId, instance);
    }

    @Override
    public List<ClientInformation> getAll() {
        return new ArrayList<>(map.values());
    }
}
