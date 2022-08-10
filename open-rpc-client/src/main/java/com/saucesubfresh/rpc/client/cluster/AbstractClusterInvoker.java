package com.saucesubfresh.rpc.client.cluster;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lijunping on 2022/1/21
 */
@Slf4j
public abstract class AbstractClusterInvoker implements ClusterInvoker{

    private final ServiceDiscovery serviceDiscovery;
    protected final ClientConfiguration configuration;
    protected final LoadBalance loadBalance;
    protected final RemotingInvoker remotingInvoker;

    public AbstractClusterInvoker(ServiceDiscovery serviceDiscovery, ClientConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker){
        this.serviceDiscovery = serviceDiscovery;
        this.configuration = configuration;
        this.loadBalance = loadBalance;
        this.remotingInvoker = remotingInvoker;
    }

    @Override
    public MessageResponseBody invoke(Message messages) throws RpcException {
        final List<ServerInformation> clientList = lookup();
        return doInvoke(messages, clientList);
    }

    /**
     * 通过服务发现找到所有在线的服务端
     */
    protected List<ServerInformation> lookup() {
        List<ServerInformation> clients = serviceDiscovery.lookup();
        if (CollectionUtils.isEmpty(clients)) {
            throw new RpcException("No healthy server were found.");
        }
        return clients;
    }

    /**
     * 通过负载均衡策略找出合适的服务端进行调用
     */
    protected ServerInformation select(Message message, List<ServerInformation> clients) throws RpcException{
        return loadBalance.select(message, clients);
    }

    protected abstract MessageResponseBody doInvoke(Message message, List<ServerInformation> clients) throws RpcException;
}
