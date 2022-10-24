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
package com.saucesubfresh.rpc.client.store.support;

import com.saucesubfresh.rpc.client.store.AbstractInstanceStore;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lijunping on 2022/2/17
 */
public class LocalInstanceStore extends AbstractInstanceStore {

    private final Map<String, List<ServerInformation>> store = PlatformDependent.newConcurrentHashMap();

    @Override
    public void put(String namespace, List<ServerInformation> instances) {
        store.put(namespace, super.handler(namespace, instances));
    }

    @Override
    public List<ServerInformation> getByNamespace(String namespace) {
        return store.getOrDefault(namespace, new ArrayList<>());
    }
}
