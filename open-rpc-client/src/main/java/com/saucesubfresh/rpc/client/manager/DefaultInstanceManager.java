package com.saucesubfresh.rpc.client.manager;

import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lijunping on 2022/8/17
 */
@Slf4j
public class DefaultInstanceManager implements InstanceManager{

    private final RemotingInvoker remotingInvoker;

    public DefaultInstanceManager(RemotingInvoker remotingInvoker) {
        this.remotingInvoker = remotingInvoker;
    }

    @Override
    public boolean offlineServer(String serverId){
        final String[] clientInfo = StringUtils.split(serverId, CommonConstant.Symbol.MH);
        ServerInformation serverInformation = ServerInformation.valueOf(clientInfo[0], Integer.parseInt(clientInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.DEREGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, serverInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }

    @Override
    public boolean onlineServer(String serverId){
        final String[] clientInfo = StringUtils.split(serverId, CommonConstant.Symbol.MH);
        ServerInformation serverInformation = ServerInformation.valueOf(clientInfo[0], Integer.parseInt(clientInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.REGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, serverInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }
}
