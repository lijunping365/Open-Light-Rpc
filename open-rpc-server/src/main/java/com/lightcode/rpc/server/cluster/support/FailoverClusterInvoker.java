package com.lightcode.rpc.server.cluster.support;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.server.ServerConfiguration;
import com.lightcode.rpc.server.cluster.AbstractClusterInvoker;
import com.lightcode.rpc.server.discovery.ServiceDiscovery;
import com.lightcode.rpc.server.loadbalance.LoadBalance;
import com.lightcode.rpc.server.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 故障转移模式
 * @author: 李俊平
 * @Date: 2022-02-02 08:40
 */
@Slf4j
public class FailoverClusterInvoker extends AbstractClusterInvoker {

    public FailoverClusterInvoker(ServiceDiscovery serviceDiscovery, ServerConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Override
    protected void doInvoke(Message message, List<ClientInformation> clients) throws RpcException {
        ClientInformation clientInformation = select(message, clients);
        try {
            remotingInvoker.invoke(message, clientInformation);
        } catch (RpcException e){
            clients.remove(clientInformation);
            if (CollectionUtils.isEmpty(clients)){
                throw new RpcException(e.getMessage());
            }
            invoke(message, clients);
        }
    }

    private void invoke(Message message, List<ClientInformation> clients) throws RpcException{
        RpcException ex = null;
        for (ClientInformation clientInformation : clients) {
            try {
                remotingInvoker.invoke(message, clientInformation);
                if (ex != null){
                    throw new RpcException(ex.getMessage());
                }
                return;
            }catch (RpcException e){
                ex = e;
            } catch (Throwable e) {
                ex = new RpcException(e.getMessage());
            }
        }
    }
}
