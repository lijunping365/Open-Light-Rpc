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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 失败重试调用模式
 * @author: 李俊平
 * @Date: 2022-01-31 19:29
 */
@Slf4j
public class FailbackClusterInvoker extends AbstractClusterInvoker {

    public FailbackClusterInvoker(ServiceDiscovery serviceDiscovery, ClientConfiguration configuration, LoadBalance loadBalance, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalance, remotingInvoker);
    }

    @Override
    protected MessageResponseBody doInvoke(Message message, List<ServerInformation> clients) throws RpcException {
        ServerInformation serverInformation = select(message, clients);
        boolean success = false;
        int maxTimes = configuration.getRetryTimes();
        int currentTimes = 0;
        MessageResponseBody response = null;
        while (!success) {
            try {
                response = remotingInvoker.invoke(message, serverInformation);
                success = true;
            }catch (RpcException e){
                log.error(e.getMessage(), e);
            }
            if (!success) {
                currentTimes++;
                if (currentTimes > maxTimes) {
                    throw new RpcException("The number of invoke retries reaches the upper limit, " +
                            "the maximum number of times：" + maxTimes);
                }
                try {
                    Thread.sleep(configuration.getRetryIntervalMilliSeconds());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}
