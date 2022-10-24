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
package com.saucesubfresh.rpc.client.cluster;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;
import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.discovery.ServiceDiscovery;
import com.saucesubfresh.rpc.client.loadbalance.LoadBalance;
import com.saucesubfresh.rpc.client.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lijunping on 2022/1/21
 */
@Slf4j
public abstract class AbstractClusterInvoker implements ClusterInvoker{

    private final ServiceDiscovery serviceDiscovery;
    protected final ClientConfiguration configuration;
    protected final LoadBalance loadBalance;
    protected final RemotingInvoker remotingInvoker;

    public AbstractClusterInvoker(ServiceDiscovery serviceDiscovery, ClientConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker){
        this.serviceDiscovery = serviceDiscovery;
        this.configuration = configuration;
        this.loadBalance = loadBalance;
        this.remotingInvoker = remotingInvoker;
    }

    @Override
    public MessageResponseBody invoke(Message messages) throws RpcException {
        final String namespace = messages.getNamespace();
        final List<ServerInformation> clientList = lookup(namespace);
        return doInvoke(messages, clientList);
    }

    /**
     * 通过服务发现找到所有在线的服务端
     */
    protected List<ServerInformation> lookup(String namespace) {
        List<ServerInformation> clients = serviceDiscovery.lookup(namespace);
        if (CollectionUtils.isEmpty(clients)) {
            throw new RpcException("No healthy server were found.");
        }
        return clients;
    }

    /**
     * 通过负载均衡策略找出合适的服务端进行调用
     */
    protected ServerInformation select(Message message, List<ServerInformation> clients) throws RpcException{
        return loadBalance.select(message, clients);
    }

    protected abstract MessageResponseBody doInvoke(Message message, List<ServerInformation> clients) throws RpcException;
}
