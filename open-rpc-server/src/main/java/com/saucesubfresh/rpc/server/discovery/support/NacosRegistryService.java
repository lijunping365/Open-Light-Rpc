package com.saucesubfresh.rpc.server.discovery.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.server.store.InstanceStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 李俊平
 * @Date: 2021-10-31 14:50
 */
@Slf4j
public class NacosRegistryService extends AbstractServiceDiscovery implements DisposableBean, EventListener {
    private static final String REMOVER = "DEFAULT_GROUP@@";
    private final NamingService namingService;

    public NacosRegistryService(NamingService namingService, InstanceStore instanceStore) {
        super(instanceStore);
        this.namingService = namingService;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof NamingEvent)) {
            return;
        }
        NamingEvent namingEvent = (NamingEvent) event;
        String serviceName = namingEvent.getServiceName();
        String namespace = serviceName.replace(REMOVER, StringUtils.EMPTY);
        List<Instance> instances = namingEvent.getInstances();
        List<ClientInformation> clients = convertClientInformation(instances);
        updateCache(namespace, clients);
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
    public void subscribe(List<String> namespaces) {
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

    private List<ClientInformation> convertClientInformation(List<Instance> instances){
        if (CollectionUtils.isEmpty(instances)){
            return Collections.emptyList();
        }
        return instances.stream()
                .map(instance -> ClientInformation.valueOf(instance.getIp(), instance.getPort()))
                .collect(Collectors.toList());
    }
}
