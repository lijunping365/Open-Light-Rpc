package com.saucesubfresh.rpc.client.discovery;

import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: 李俊平
 * @Date: 2022-01-30 09:36
 */
@Slf4j
public abstract class AbstractServiceDiscovery implements ServiceDiscovery{

    private final InstanceStore instanceStore;

    protected AbstractServiceDiscovery(InstanceStore instanceStore) {
        this.instanceStore = instanceStore;
    }

    @Override
    public List<ServerInformation> lookup(String namespace){
        List<ServerInformation> servers = instanceStore.getOnlineList(namespace);
        if (!CollectionUtils.isEmpty(servers)){
            return servers;
        }
        servers = doLookup(namespace);
        updateCache(namespace, servers);
        return servers;
    }

    protected void updateCache(String namespace, List<ServerInformation> instances){
        instanceStore.put(namespace, instances);
    }

    protected abstract List<ServerInformation> doLookup(String namespace);
}
