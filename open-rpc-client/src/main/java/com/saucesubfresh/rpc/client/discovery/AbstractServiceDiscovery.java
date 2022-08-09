package com.saucesubfresh.rpc.client.discovery;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: 李俊平
 * @Date: 2022-01-30 09:36
 */
@Slf4j
public abstract class AbstractServiceDiscovery implements ServiceDiscovery{

    private static final String SPLIT_SYMBOL = "::";

    private final RemotingInvoker remotingInvoker;
    private final InstanceStore instanceStore;
    protected final ClientConfiguration configuration;

    protected AbstractServiceDiscovery(RemotingInvoker remotingInvoker, InstanceStore instanceStore, ClientConfiguration configuration) {
        if (StringUtils.isBlank(configuration.getClientName())){
            throw new RpcException("The subscribe client name cannot be empty.");
        }
        this.remotingInvoker = remotingInvoker;
        this.instanceStore = instanceStore;
        this.configuration = configuration;
    }

    @Override
    public List<ClientInformation> lookup(){
        List<ClientInformation> clients = instanceStore.getOnlineList();
        if (!CollectionUtils.isEmpty(clients)){
            return clients;
        }
        return doLookup();
    }

    @Override
    public boolean offlineClient(String clientId){
        final String[] clientInfo = StringUtils.split(clientId, SPLIT_SYMBOL);
        ClientInformation clientInformation = ClientInformation.valueOf(clientInfo[0], Integer.parseInt(clientInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.DEREGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, clientInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }

    @Override
    public boolean onlineClient(String clientId){
        final String[] clientInfo = StringUtils.split(clientId, SPLIT_SYMBOL);
        ClientInformation clientInformation = ClientInformation.valueOf(clientInfo[0], Integer.parseInt(clientInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.REGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, clientInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }

    protected void updateCache(List<ClientInformation> instances){
        instanceStore.put(instances);
    }

    protected abstract List<ClientInformation> doLookup();
}
