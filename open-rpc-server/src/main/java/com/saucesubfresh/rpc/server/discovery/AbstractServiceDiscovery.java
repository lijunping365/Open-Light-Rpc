package com.saucesubfresh.rpc.server.discovery;

import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.store.InstanceStore;
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
    public List<ClientInformation> lookup(String namespace){
        List<ClientInformation> clients = instanceStore.getOnlineList(namespace);
        if (!CollectionUtils.isEmpty(clients)){
            return clients;
        }
        return doLookup(namespace);
    }

    protected void updateCache(String namespace, List<ClientInformation> instances){
        instanceStore.put(namespace, instances);
    }

    protected abstract List<ClientInformation> doLookup(String namespace);
}
