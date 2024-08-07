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
package com.openbytecode.rpc.discovery;

import com.openbytecode.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lijunping 2022-01-30 09:36
 */
@Slf4j
public abstract class AbstractServiceDiscovery implements ServiceDiscovery{

    private final ServiceManager serviceManager;

    protected AbstractServiceDiscovery(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public List<ServerInformation> lookup(String namespace){
        List<ServerInformation> onlineServers = serviceManager.getOnlineList(namespace);
        if (!CollectionUtils.isEmpty(onlineServers)){
            return onlineServers;
        }
        onlineServers = doLookup(namespace);
        if (!CollectionUtils.isEmpty(onlineServers)){
            updateService(namespace, onlineServers);
        }
        return onlineServers;
    }

    protected void updateService(String namespace, List<ServerInformation> instances){
        serviceManager.put(namespace, instances);
    }

    protected abstract List<ServerInformation> doLookup(String namespace);
}
