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

import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2021/12/3
 */
@Slf4j
public class ZookeeperServiceDiscovery extends AbstractServiceDiscovery implements DisposableBean{
    private final ZkClient zkClient;

    public ZookeeperServiceDiscovery(ZkClient zkClient, ServiceManager serviceManager){
        super(serviceManager);
        this.zkClient = zkClient;
    }

    @Override
    public void subscribe(List<String> namespaces){
        if (CollectionUtils.isEmpty(namespaces)){
            return;
        }
        namespaces.forEach(namespace-> zkClient.subscribeChildChanges(namespace, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> children) throws Exception {
                List<ServerInformation> onlineServers = convert(children);
                log.info("zookeeper parentPath {}, current online instance {}", parentPath, onlineServers);
                updateService(namespace, onlineServers);
            }
        }));
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        List<ServerInformation> onlineServers = new ArrayList<>();
        try {
            List<String> children = zkClient.getChildren(namespace);
            onlineServers = convert(children);
            log.info("lookup online instance {}", onlineServers);
        }catch (Exception e){
            log.error("lookup instance failed {}", e.getMessage());
        }
        return onlineServers;
    }

    @Override
    public void destroy() throws Exception {
        this.zkClient.close();
    }

    private List<ServerInformation> convert(List<String> instances){
        if (CollectionUtils.isEmpty(instances)){
            return new ArrayList<>();
        }
        return instances.stream().map(instance -> {
            long currentTime = System.currentTimeMillis();
            String[] split = StringUtils.split(instance, CommonConstant.Symbol.MH);
            ServerInformation serverInfo = ServerInformation.valueOf(split[0], Integer.parseInt(split[1]));
            serverInfo.setStatus(Status.ON_LINE);
            serverInfo.setOnlineTime(currentTime);
            return serverInfo;
        }).collect(Collectors.toList());
    }
}
