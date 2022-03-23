package com.lightcode.rpc.server.loadbalance.support;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.server.loadbalance.AbstractLoadBalance;
import com.lightcode.rpc.server.loadbalance.LoadBalance;
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
     * @return Load-balanced {@link ClientInformation}
     * @throws RpcException message pipe exception
     */
    @Override
    public ClientInformation doSelect(Message message, List<ClientInformation> clients) throws RpcException {
        TreeMap<Double, ClientInformation> nodes = new TreeMap<>();
        clients.forEach(node -> {
            double lastWeight = nodes.size() == 0 ? 0 : nodes.lastKey();
            nodes.put(node.getWeight() + lastWeight, node);
        });
        Double randomWeight = nodes.lastKey() * Math.random();
        SortedMap<Double, ClientInformation> tailMap = nodes.tailMap(randomWeight, false);
        if (ObjectUtils.isEmpty(tailMap)) {
            throw new RpcException("No load balancing node was found");
        }
        return nodes.get(tailMap.firstKey());
    }
}
