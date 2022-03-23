package com.lightcode.rpc.server.cluster.support;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.server.ServerConfiguration;
import com.lightcode.rpc.server.cluster.AbstractClusterInvoker;
import com.lightcode.rpc.server.discovery.ServiceDiscovery;
import com.lightcode.rpc.server.loadbalance.LoadBalance;
import com.lightcode.rpc.server.remoting.RemotingInvoker;

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
    protected void doInvoke(Message message, List<ClientInformation> clients) throws RpcException {
        clients.forEach(clientInformation -> remotingInvoker.invoke(message, clientInformation));
    }
}
