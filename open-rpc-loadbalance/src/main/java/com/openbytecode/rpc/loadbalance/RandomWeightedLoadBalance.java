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
package com.openbytecode.rpc.loadbalance;


import com.openbytecode.rpc.core.Message;
import com.openbytecode.rpc.core.exception.RpcException;
import com.openbytecode.rpc.core.information.ServerInformation;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The {@link LoadBalance} random strategy
 *
 * @author lijunping
 */
public class RandomWeightedLoadBalance extends AbstractLoadBalance {

    @Override
    public ServerInformation doSelect(Message message, List<ServerInformation> clients) throws RpcException {
        TreeMap<Double, ServerInformation> nodes = new TreeMap<>();
        clients.forEach(node -> {
            double lastWeight = nodes.size() == 0 ? 0 : nodes.lastKey();
            nodes.put(node.getWeight() + lastWeight, node);
        });
        Double randomWeight = nodes.lastKey() * Math.random();
        SortedMap<Double, ServerInformation> tailMap = nodes.tailMap(randomWeight, false);
        if (ObjectUtils.isEmpty(tailMap)) {
            throw new RpcException("No load balancing node was found");
        }
        return nodes.get(tailMap.firstKey());
    }
}
