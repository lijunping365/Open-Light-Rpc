package com.saucesubfresh.rpc.client.discovery.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 李俊平
 * @Date: 2021-10-31 14:50
 */
@Slf4j
public class NacosRegistryService extends AbstractServiceDiscovery implements InitializingBean, DisposableBean, EventListener {
    private final NamingService namingService;

    public NacosRegistryService(NamingService namingService, RemotingInvoker remotingInvoker, InstanceStore instanceStore, ClientConfiguration configuration) {
        super(remotingInvoker, instanceStore, configuration);
        this.namingService = namingService;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof NamingEvent)) {
            return;
        }
        NamingEvent namingEvent = (NamingEvent) event;
        List<Instance> instances = namingEvent.getInstances();
        List<ServerInformation> clients = convertServerInformation(instances);
        updateCache(clients);
        log.info("register successfully instance {}", clients);
    }

    @Override
    protected List<ServerInformation> doLookup() {
        try {
            List<Instance> allInstances = namingService.getAllInstances(this.configuration.getServerName());
            return convertServerInformation(allInstances);
        } catch (NacosException e) {
            log.error("lookup instance failed {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void destroy() throws Exception {
        this.namingService.shutDown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.namingService.subscribe(this.configuration.getServerName(), this);
    }

    private List<ServerInformation> convertServerInformation(List<Instance> instances){
        if (CollectionUtils.isEmpty(instances)){
            return Collections.emptyList();
        }
        return instances.stream()
                .map(instance -> ServerInformation.valueOf(instance.getIp(), instance.getPort()))
                .collect(Collectors.toList());
    }
}
