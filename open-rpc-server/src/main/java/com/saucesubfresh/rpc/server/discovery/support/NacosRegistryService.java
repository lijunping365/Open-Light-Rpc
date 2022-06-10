package com.saucesubfresh.rpc.server.discovery.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.server.namespace.NamespaceService;
import com.saucesubfresh.rpc.server.store.InstanceStore;
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
    private final NamespaceService namespaceService;

    public NacosRegistryService(NamingService namingService, InstanceStore instanceStore, NamespaceService namespaceService) {
        super(instanceStore);
        this.namingService = namingService;
        this.namespaceService = namespaceService;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof NamingEvent)) {
            return;
        }
        NamingEvent namingEvent = (NamingEvent) event;
        final String serviceName = namingEvent.getServiceName();
        List<Instance> instances = namingEvent.getInstances();
        List<ClientInformation> clients = convertClientInformation(instances);
        updateCache(serviceName, clients);
        log.info("register successfully instance {}", clients);
    }

    @Override
    protected List<ClientInformation> doLookup(String namespace) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(namespace);
            return convertClientInformation(allInstances);
        } catch (NacosException e) {
            log.error("lookup instance failed {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    protected void subscribe() {
        final List<String> namespaces = namespaceService.loadNamespace();
        if (CollectionUtils.isEmpty(namespaces)){
            return;
        }
        namespaces.forEach(namespace->{
            try {
                this.namingService.subscribe(namespace, this);
            } catch (NacosException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        this.namingService.shutDown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.subscribe();
    }

    private List<ClientInformation> convertClientInformation(List<Instance> instances){
        if (CollectionUtils.isEmpty(instances)){
            return Collections.emptyList();
        }
        return instances.stream()
                .map(instance -> ClientInformation.valueOf(instance.getIp(), instance.getPort()))
                .collect(Collectors.toList());
    }
}
