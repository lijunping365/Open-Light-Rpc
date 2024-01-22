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


import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * 服务发现
 *
 * @author lijunping on 2022/1/20
 */
public interface ServiceDiscovery {

    /**
     * 查询实时在线服务端列表
     * @param namespace 应用名称
     * @return
     */
    List<ServerInformation> lookup(String namespace);

    /**
     * 注册监听
     * @param namespaces
     */
    void subscribe(List<String> namespaces);
}
