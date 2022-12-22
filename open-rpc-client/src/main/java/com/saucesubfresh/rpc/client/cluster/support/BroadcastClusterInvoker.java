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


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.cluster.AbstractClusterInvoker;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;

import java.util.List;

/**
 * 广播调用模式
 *
 * @author lijunping on 2022/1/20
 */
public class BroadcastClusterInvoker extends AbstractClusterInvoker {

    public BroadcastClusterInvoker(ServiceDiscovery serviceDiscovery, ClientConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Override
    protected MessageResponseBody doInvoke(Message message, List<ServerInformation> servers) throws RpcException {
        servers.forEach(clientInformation -> remotingInvoker.invoke(message, clientInformation));
        return new MessageResponseBody();
    }
}
