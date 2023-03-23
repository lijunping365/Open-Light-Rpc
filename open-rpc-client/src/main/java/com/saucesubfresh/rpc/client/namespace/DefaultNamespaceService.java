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
package com.saucesubfresh.rpc.client.namespace;

import com.saucesubfresh.rpc.client.ClientConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * 默认从配置文件中加载
 *
 * @author lijunping on 2022/8/17
 */
public class DefaultNamespaceService implements NamespaceService{

    private final ClientConfiguration clientConfiguration;

    public DefaultNamespaceService(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public List<String> loadNamespace() {
        return Collections.singletonList(clientConfiguration.getServerName());
    }
}
