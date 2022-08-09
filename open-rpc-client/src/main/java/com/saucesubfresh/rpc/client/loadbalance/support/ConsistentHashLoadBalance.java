package com.saucesubfresh.rpc.client.loadbalance.support;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.client.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 分组下机器地址相同，不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；
 * a、virtual node：解决不均衡问题
 * b、hash method replace hashCode：String 的 hashCode可能重复，需要进一步扩大 hashCode 的取值范围
 * @author lijunping on 2021/9/1
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ClientInformation doSelect(Message message, List<ClientInformation> invokers) throws RpcException {
        TreeMap<Long, ClientInformation> clientRing = new TreeMap<>();
        for (ClientInformation invoker: invokers) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash(md5("SHARD-" + invoker.getAddress() + "-NODE-" + i));
                clientRing.put(addressHash, invoker);
            }
        }

        long jobHash = hash(md5(message.getMsgId()));
        SortedMap<Long, ClientInformation> lastRing = clientRing.tailMap(jobHash);
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
