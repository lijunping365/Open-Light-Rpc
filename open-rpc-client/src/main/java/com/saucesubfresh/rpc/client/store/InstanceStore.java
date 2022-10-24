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
package com.saucesubfresh.rpc.client.store;



import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务端实例缓存
 * @author lijunping on 2022/2/17
 */
public interface InstanceStore {

    /**
     * @param namespace 应用名称
     * @param instances 要上线的服务端列表
     */
    void put(String namespace, List<ServerInformation> instances);

    /**
     * @return 返回缓存中全部的服务端列表
     */
    List<ServerInformation> getByNamespace(String namespace);

    /**
     * @return 返回缓存中在线的服务端列表
     */
    default List<ServerInformation> getOnlineList(String namespace){
        List<ServerInformation> serverInstances = getByNamespace(namespace);
        if (CollectionUtils.isEmpty(serverInstances)){
            return Collections.emptyList();
        }
        return serverInstances.stream().filter(e->e.getStatus() == Status.ON_LINE).collect(Collectors.toList());
    }
}
