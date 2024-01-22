/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saucesubfresh.rpc.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijunping 2021-10-31 14:50
 */
@Slf4j
public class NacosServiceDiscovery extends AbstractServiceDiscovery implements DisposableBean, EventListener {
    private static final String REMOVER = "DEFAULT_GROUP@@";
    private final NamingService namingService;

    public NacosServiceDiscovery(NamingService namingService, ServiceManager serviceManager) {
        super(serviceManager);
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
        List<ServerInformation> onlineServers = convert(instances);
        log.info("current online instance {}", onlineServers);
        updateService(namespace, onlineServers);
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        List<ServerInformation> onlineServers = new ArrayList<>();
        try {
            List<Instance> allInstances = namingService.getAllInstances(namespace);
            onlineServers = convert(allInstances);
            log.info("lookup online instance {}", onlineServers);
        } catch (NacosException e) {
            log.error("lookup instance failed {}", e.getMessage());
        }
        return onlineServers;
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
