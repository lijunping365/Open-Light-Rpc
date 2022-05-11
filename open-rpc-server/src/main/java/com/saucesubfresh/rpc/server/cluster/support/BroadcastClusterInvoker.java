package com.saucesubfresh.rpc.server.cluster.support;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.cluster.AbstractClusterInvoker;
import com.saucesubfresh.rpc.server.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.server.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;

import java.util.List;

/**
 * 广播调用模式
 * @author lijunping on 2022/1/20
 */
public class BroadcastClusterInvoker extends AbstractClusterInvoker {

    public BroadcastClusterInvoker(ServiceDiscovery serviceDiscovery, ServerConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Override
    protected MessageResponseBody doInvoke(Message message, List<ClientInformation> clients) throws RpcException {
        clients.forEach(clientInformation -> remotingInvoker.invoke(message, clientInformation));
        return new MessageResponseBody();
    }
}
