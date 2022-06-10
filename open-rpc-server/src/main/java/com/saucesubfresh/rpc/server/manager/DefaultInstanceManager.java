package com.saucesubfresh.rpc.server.manager;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lijunping on 2022/6/10
 */
public class DefaultInstanceManager implements InstanceManager{

    private static final String SPLIT_SYMBOL = "::";

    private final RemotingInvoker remotingInvoker;
    public DefaultInstanceManager(RemotingInvoker remotingInvoker) {
        this.remotingInvoker = remotingInvoker;
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
}
