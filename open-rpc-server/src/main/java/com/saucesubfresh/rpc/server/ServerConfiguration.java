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
package com.saucesubfresh.rpc.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务端配置
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.server")
public class ServerConfiguration {
    /**
     * The server address
     */
    private String serverAddress = "localhost";
    /**
     * The server port
     */
    private int serverPort = 5200;
    /**
     * The name of the service registered to the nacos or zookeeper
     * if you use nacos，You should be named like this: job-server-services
     * if you use zookeeper, You should be named like this: /JobServer
     */
    private String serverName;
}
