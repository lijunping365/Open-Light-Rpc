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
    protected MessageResponseBody doInvoke(Message message, List<ClientInformation> clients) throws RpcException {
        ClientInformation clientInformation = select(message, clients);
        MessageResponseBody response;
        try {
            response = remotingInvoker.invoke(message, clientInformation);
        } catch (RpcException e){
            clients.remove(clientInformation);
            if (CollectionUtils.isEmpty(clients)){
                throw new RpcException(e.getMessage());
            }
            response = invoke(message, clients);
        }
        return response;
    }

    private MessageResponseBody invoke(Message message, List<ClientInformation> clients) throws RpcException{
        RpcException ex = null;
        MessageResponseBody response = null;
        for (ClientInformation clientInformation : clients) {
            try {
                response = remotingInvoker.invoke(message, clientInformation);
                break;
            }catch (RpcException e){
                ex = e;
            }
        }
        if (response == null && ex != null){
            throw new RpcException(ex.getMessage());
        }
        return response;
    }
}
