/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        final String[] serverInfo = StringUtils.split(serverId, CommonConstant.Symbol.MH);
        ServerInformation serverInformation = ServerInformation.valueOf(serverInfo[0], Integer.parseInt(serverInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.DEREGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, serverInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }

    @Override
    public boolean onlineServer(String serverId){
        final String[] serverInfo = StringUtils.split(serverId, CommonConstant.Symbol.MH);
        ServerInformation serverInformation = ServerInformation.valueOf(serverInfo[0], Integer.parseInt(serverInfo[1]));
        Message message = new Message();
        message.setCommand(PacketType.REGISTER);
        MessageResponseBody invoke = remotingInvoker.invoke(message, serverInformation);
        return invoke.getStatus() == ResponseStatus.SUCCESS;
    }
}
