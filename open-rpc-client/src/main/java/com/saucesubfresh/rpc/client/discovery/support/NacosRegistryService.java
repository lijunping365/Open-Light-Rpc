package com.saucesubfresh.rpc.client.discovery.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.client.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.client.namespace.NamespaceService;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 李俊平
 * @Date: 2021-10-31 14:50
 */
@Slf4j
public class NacosRegistryService extends AbstractServiceDiscovery implements InitializingBean, DisposableBean, EventListener {
    private static final String REMOVER = "DEFAULT_GROUP@@";
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
        String serviceName = namingEvent.getServiceName();
        String namespace = serviceName.replace(REMOVER, StringUtils.EMPTY);
        List<Instance> instances = namingEvent.getInstances();
        List<ServerInformation> onlineServers = convert(instances);
        updateCache(namespace, onlineServers);
        log.info("register successfully instance {}", onlineServers);
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(namespace);
            return convert(allInstances);
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
                log.error("subscribe namespace failed: {}",e.getMessage());
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            List<String> namespaces = namespaceService.loadNamespace();
            this.subscribe(namespaces);
        }catch (Exception e){
            log.error("load namespace failed: {}", e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        this.namingService.shutDown();
    }

    private List<ServerInformation> convert(List<Instance> instances){
        if (CollectionUtils.isEmpty(instances)){
            return new ArrayList<>();
        }
        return instances.stream().map(instance -> {
            long currentTime = System.currentTimeMillis();
            ServerInformation serverInfo = ServerInformation.valueOf(instance.getIp(), instance.getPort());
            serverInfo.setStatus(Status.ON_LINE);
            serverInfo.setOnlineTime(currentTime);
            return serverInfo;
        }).collect(Collectors.toList());
    }
}
