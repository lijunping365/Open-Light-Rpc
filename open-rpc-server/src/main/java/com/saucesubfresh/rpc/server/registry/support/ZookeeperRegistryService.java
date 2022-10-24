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
package com.saucesubfresh.rpc.server.registry.support;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.registry.AbstractRegistryService;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.DisposableBean;

import java.util.HashMap;
import java.util.Map;
/**
 * Register to Server in Zookeeper mode
 *
 * @author lijunping on 2021/12/2
 */
@Slf4j
public class ZookeeperRegistryService extends AbstractRegistryService implements DisposableBean {

    private final ZkClient zkClient;

    public ZookeeperRegistryService(ZkClient zkClient, ServerConfiguration configuration) {
        super(configuration);
        this.zkClient = zkClient;
    }

    @Override
    public void doRegister(String serverAddress, int serverPort) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("serverIp", serverAddress);
        metadata.put("serverPort", String.valueOf(serverPort));
        if (!zkClient.exists(this.configuration.getServerName())) {
            zkClient.createPersistent(this.configuration.getServerName(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        String serverInfo = String.format(CommonConstant.ADDRESS_PATTERN, serverAddress, serverPort);
        String serverPath = this.configuration.getServerName() + CommonConstant.Symbol.SLASH + serverInfo;
        if (!zkClient.exists(serverPath)) {
            zkClient.createEphemeral(serverPath, metadata, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        log.info("Current server registered to zookeeper server successfully.");
    }

    @Override
    public void deRegister(String serverAddress, int serverPort) {
        String serverInfo = String.format(CommonConstant.ADDRESS_PATTERN, serverAddress, serverPort);
        String serverPath = this.configuration.getServerName() + CommonConstant.Symbol.SLASH + serverInfo;
        this.zkClient.delete(serverPath);
    }

    @Override
    public void destroy() {
        try {
            zkClient.close();
        } catch (Exception e) {
            log.warn("Failed to close zookeeper client, cause: " + e.getMessage(), e);
        }
    }
}
