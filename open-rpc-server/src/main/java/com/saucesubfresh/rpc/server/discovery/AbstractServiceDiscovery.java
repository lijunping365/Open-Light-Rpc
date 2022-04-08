package com.saucesubfresh.rpc.server.discovery;

import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.enums.PacketType;
import com.lightcode.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.server.store.InstanceStore;
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

    protected AbstractServiceDiscovery(RemotingInvoker remotingInvoker, InstanceStore instanceStore) {
        this.remotingInvoker = remotingInvoker;
        this.instanceStore = instanceStore;
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
        remotingInvoker.invoke(message, clientInformation);
        return true;
    }

    public boolean onlineClient(String clientId){
        final String[] clientInfo = StringUtils.split(clientId, SPLIT_SYMBOL);
        ClientInformation clientInformation = ClientInformation.valueOf(clientInfo[0], Integer.parseInt(clientInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.REGISTER);
        remotingInvoker.invoke(message, clientInformation);
        return true;
    }

    protected void updateCache(List<ClientInformation> instances){
        instanceStore.put(instances);
    }

    protected abstract List<ClientInformation> doLookup();
}
