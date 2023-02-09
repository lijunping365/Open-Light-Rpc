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
package com.saucesubfresh.rpc.server.registry;

import com.saucesubfresh.rpc.core.exception.ConfigurationException;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author lijunping on 2022/1/24
 */
public abstract class AbstractRegistryService implements RegistryService, InitializingBean {

    protected final ServerConfiguration configuration;

    public AbstractRegistryService(ServerConfiguration configuration){
        if (configuration.getServerPort() <= 0 || configuration.getServerPort() > 65535) {
            throw new ConfigurationException("The Server port must be greater than 0 and less than 65535.");
        }
        if (StringUtils.isBlank(configuration.getServerAddress())) {
            throw new ConfigurationException("The Target server address cannot be empty.");
        }
        if (StringUtils.isBlank(configuration.getServerName())){
            throw new ConfigurationException("The client register name cannot be empty.");
        }
        this.configuration = configuration;
    }
    @Override
    public void register(String serverAddress, int serverPort) {
        doRegister(serverAddress, serverPort);
    }

    /**
     * Register current client to server
     * <p>
     * Use child threads for registration logic
     *
     */
    private void registerToServer() {
        Thread thread = new Thread(() -> this.register(configuration.getServerAddress(), configuration.getServerPort()));
        thread.start();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerToServer();
    }

    protected abstract void doRegister(String serverAddress, int serverPort);
}
