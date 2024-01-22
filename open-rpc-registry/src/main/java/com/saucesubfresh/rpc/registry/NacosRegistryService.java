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
package com.saucesubfresh.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.core.exception.ServerRegisterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Register to Server in Nacos mode
 *
 * @author lijunping 2021-10-31 14:38
 */
@Slf4j
public class NacosRegistryService extends AbstractRegistryService implements DisposableBean{

    private final NamingService namingService;

    public NacosRegistryService(NamingService namingService) {
        this.namingService = namingService;
    }
    /**
     * Register server to nacos server
     * <p>
     * If there is an instance of {@link NamingService} in the running project,
     * use it directly, otherwise create a new instance
     *
     * @param serverName  The server name
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    @Override
    public void register(String serverName, String serverAddress, int serverPort) {
        super.validate(serverName, serverAddress, serverPort);
        try {
            Instance instance = new Instance();
            instance.setIp(serverAddress);
            instance.setPort(serverPort);
            Map<String, String> metadata = new HashMap<>();
            instance.setMetadata(metadata);
            this.namingService.registerInstance(serverName, instance);
            log.info("Current server registered to nacos server successfully.");
        } catch (Exception e) {
            log.error("register instance failed {}", e.getMessage());
            throw new ServerRegisterException(e.getMessage());
        }
    }

    @Override
    public void deRegister(String serverName, String serverAddress, int serverPort) {
        try {
            this.namingService.deregisterInstance(serverName, serverAddress, serverPort);
        } catch (NacosException e) {
            log.error("deRegister instance failed {}", e.getMessage());
            throw new ServerRegisterException(e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        this.namingService.shutDown();
        log.info("The server is successfully offline from the nacos server.");
    }
}
