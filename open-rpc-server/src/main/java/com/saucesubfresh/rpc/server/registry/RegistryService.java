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

/**
 * @author lijunping on 2022/1/24
 */
public interface RegistryService {

    /**
     * Register server
     *
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    void register(String serverAddress, int serverPort);

    /**
     * deRegister server
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    void deRegister(String serverAddress, int serverPort);
}
