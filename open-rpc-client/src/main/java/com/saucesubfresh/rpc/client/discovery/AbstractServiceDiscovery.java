package com.saucesubfresh.rpc.client.discovery;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: 李俊平
 * @Date: 2022-01-30 09:36
 */
@Slf4j
public abstract class AbstractServiceDiscovery implements ServiceDiscovery{

    private final InstanceStore instanceStore;
    protected final ClientConfiguration configuration;

    protected AbstractServiceDiscovery(InstanceStore instanceStore, ClientConfiguration configuration) {
        if (StringUtils.isBlank(configuration.getServerName())){
            throw new RpcException("The subscribe server name cannot be empty.");
        }
        this.instanceStore = instanceStore;
        this.configuration = configuration;
    }

    @Override
    public List<ServerInformation> lookup(){
        List<ServerInformation> onlineServers = instanceStore.getOnlineList();
        if (!CollectionUtils.isEmpty(onlineServers)){
            return onlineServers;
        }

        onlineServers = doLookup();
        if (!CollectionUtils.isEmpty(onlineServers)){
            updateCache(onlineServers);
        }
        return onlineServers;
    }

    protected void updateCache(List<ServerInformation> instances){
        instanceStore.put(instances);
    }

    protected abstract List<ServerInformation> doLookup();
}
