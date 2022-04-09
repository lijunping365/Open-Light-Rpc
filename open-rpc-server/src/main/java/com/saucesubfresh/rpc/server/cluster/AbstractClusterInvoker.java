package com.saucesubfresh.rpc.server.cluster;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.server.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lijunping on 2022/1/21
 */
@Slf4j
public abstract class AbstractClusterInvoker implements ClusterInvoker{

    private final ServiceDiscovery serviceDiscovery;
    protected final ServerConfiguration configuration;
    protected final LoadBalance loadBalance;
    protected final RemotingInvoker remotingInvoker;

    public AbstractClusterInvoker(ServiceDiscovery serviceDiscovery, ServerConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker){
        this.serviceDiscovery = serviceDiscovery;
        this.configuration = configuration;
        this.loadBalance = loadBalance;
        this.remotingInvoker = remotingInvoker;
    }

    @Override
    public void invoke(Message messages) throws RpcException {
        final List<ClientInformation> clientList = lookup();
        doInvoke(messages, clientList);
    }

    /**
     * 通过服务发现找到所有在线的客户端
     */
    protected List<ClientInformation> lookup() {
        List<ClientInformation> clients = serviceDiscovery.lookup();
        if (CollectionUtils.isEmpty(clients)) {
            throw new RpcException("No healthy clients were found.");
        }
        return clients;
    }

    /**
     * 通过负载均衡策略找出合适的客户端进行调用
     */
    protected ClientInformation select(Message message, List<ClientInformation> clients) throws RpcException{
        return loadBalance.select(message, clients);
    }

    protected abstract void doInvoke(Message message, List<ClientInformation> clients) throws RpcException;
}
