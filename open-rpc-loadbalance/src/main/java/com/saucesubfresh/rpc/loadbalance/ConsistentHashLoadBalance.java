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
package com.saucesubfresh.rpc.loadbalance;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <pre>
 * 一致性哈希算法
 * 1. 使用虚拟节点解决不均匀问题
 * 2. 使用扰动函数进一步扩大 hashCode 的取值范围
 * 3. 不同任务均匀散列在不同机器上，保证每个任务固定调度其中一台机器
 * </pre>
 *
 * @author lijunping on 2021/9/1
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServerInformation doSelect(Message message, List<ServerInformation> invokers) throws RpcException {
        TreeMap<Long, ServerInformation> clientRing = new TreeMap<>();
        for (ServerInformation invoker: invokers) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash(md5("SHARD-" + invoker.getServerId() + "-NODE-" + i));
                clientRing.put(addressHash, invoker);
            }
        }

        long jobHash = hash(md5(message.getMsgId()));
        SortedMap<Long, ServerInformation> lastRing = clientRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }
        return clientRing.firstEntry().getValue();
    }

    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        md5.update(bytes);
        return md5.digest();
    }

    private long hash(byte[] digest) {
        return (((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF))
                & 0xFFFFFFFFL;
    }
}
