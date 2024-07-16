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
package com.openbytecode.rpc.registry;

import com.openbytecode.rpc.core.exception.ServerConfigException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lijunping on 2022/1/24
 */
public abstract class AbstractRegistryService implements RegistryService{

    protected void validate(String serverName, String serverAddress, int serverPort){
        if (serverPort <= 0 || serverPort > 65535) {
            throw new ServerConfigException("The server port must be greater than 0 and less than 65535.");
        }
        if (StringUtils.isBlank(serverAddress)) {
            throw new ServerConfigException("The server address cannot be empty.");
        }
        if (StringUtils.isBlank(serverName)){
            throw new ServerConfigException("The server name cannot be empty.");
        }
    }

}
