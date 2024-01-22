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

import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2022/2/17
 */
public class DefaultServiceManager implements ServiceManager{

    private final Map<String, List<ServerInformation>> store = PlatformDependent.newConcurrentHashMap();

    @Override
    public void put(String namespace, List<ServerInformation> instances) {
        store.put(namespace, handler(namespace, instances));
    }

    @Override
    public List<ServerInformation> getByNamespace(String namespace) {
        return store.getOrDefault(namespace, new ArrayList<>());
    }

    /**
     * handler online servers
     *
     * @param onLineServers online servers
     * @return 返回 online servers  + offline servers
     */
    protected List<ServerInformation> handler(String namespace, List<ServerInformation> onLineServers){
        List<ServerInformation> instances = new ArrayList<>();
        List<ServerInformation> cacheServers = getByNamespace(namespace);
        List<String> onlineServerIds = onLineServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        List<String> cacheServerIds = cacheServers.stream().map(ServerInformation::getServerId).collect(Collectors.toList());
        cacheServerIds.removeAll(onlineServerIds);
        List<ServerInformation> offLineServers = cacheServers.stream().filter(e -> cacheServerIds.contains(e.getServerId())).collect(Collectors.toList());
        long currentTime = System.currentTimeMillis();
        offLineServers.forEach(instance->{
            instance.setStatus(Status.OFF_LINE);
            instance.setOnlineTime(currentTime);
        });
        instances.addAll(onLineServers);
        instances.addAll(offLineServers);
        return instances;
    }

}
