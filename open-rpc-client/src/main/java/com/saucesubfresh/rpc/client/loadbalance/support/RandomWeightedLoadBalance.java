package com.saucesubfresh.rpc.client.loadbalance.support;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.client.loadbalance.AbstractLoadBalance;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The {@link LoadBalance} random strategy
 *
 * @see LoadBalance
 */
public class RandomWeightedLoadBalance extends AbstractLoadBalance {

    /**
     * lookup client load-balanced address
     * Lookup according to random weight admin address
     * get firstKey by {@link SortedMap#tailMap(Object)}
     *
     *
     * @param message
     * @param clients message pipe bind clients
     * @return Load-balanced {@link ServerInformation}
     * @throws RpcException message pipe exception
     */
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
