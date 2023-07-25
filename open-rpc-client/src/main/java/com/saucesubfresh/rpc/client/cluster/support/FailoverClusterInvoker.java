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
package com.saucesubfresh.rpc.client.cluster.support;


import com.saucesubfresh.rpc.client.callback.CallCallback;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.FailoverException;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.cluster.AbstractClusterInvoker;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 故障转移模式
 *
 * @author lijunping 2022-02-02 08:40
 */
@Slf4j
public class FailoverClusterInvoker extends AbstractClusterInvoker {

    public FailoverClusterInvoker(ServiceDiscovery serviceDiscovery, ClientConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Override
    protected MessageResponseBody doInvoke(Message message, List<ServerInformation> servers, CallCallback callback) throws RpcException {
        ServerInformation serverInformation = super.select(message, servers);
        super.callback(message, serverInformation, callback);
        MessageResponseBody response;
        try {
            response = remotingInvoker.invoke(message, serverInformation);
        } catch (RpcException e){
            servers.remove(serverInformation);
            if (CollectionUtils.isEmpty(servers)){
                throw new FailoverException(serverInformation.getServerId(), e.getMessage());
            }
            response = invoke(message, servers, callback);
        }
        return response;
    }

    private MessageResponseBody invoke(Message message, List<ServerInformation> servers, CallCallback callback) throws RpcException{
        RpcException ex = null;
        MessageResponseBody response = null;
        for (ServerInformation serverInformation : servers) {
            super.callback(message, serverInformation, callback);
            try {
                response = remotingInvoker.invoke(message, serverInformation);
                break;
            }catch (RpcException e){
                ex = e;
            }
        }
        if (response == null && ex != null){
            throw new FailoverException(servers.get(servers.size() -1).getServerId(), ex.getMessage());
        }
        return response;
    }
}
