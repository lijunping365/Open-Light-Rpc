package com.lightcode.rpc.server.cluster.support;

import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.server.ServerConfiguration;
import com.lightcode.rpc.server.cluster.AbstractClusterInvoker;
import com.lightcode.rpc.server.discovery.ServiceDiscovery;
import com.lightcode.rpc.server.enums.ClusterInvokeModelEnum;
import com.lightcode.rpc.server.loadbalance.LoadBalance;
import com.lightcode.rpc.server.remoting.RemotingInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 失败重试调用模式
 * @author: 李俊平
 * @Date: 2022-01-31 19:29
 */
@Slf4j
@Component
public class FailbackClusterInvoker extends AbstractClusterInvoker {
    public FailbackClusterInvoker(ServiceDiscovery serviceDiscovery, ServerConfiguration configuration, List<LoadBalance> loadBalances, RemotingInvoker remotingInvoker) {
        super(serviceDiscovery, configuration, loadBalances, remotingInvoker);
    }

    @Override
    protected void doInvoke(Message message, List<ClientInformation> clients) throws RpcException {
        ClientInformation clientInformation = select(message, clients);
        boolean success = false;
        int maxTimes = configuration.getRetryTimes();
        int currentTimes = 0;
        while (!success) {
            try {
                remotingInvoker.invoke(message, clientInformation);
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
    }

    @Override
    public boolean support(ClusterInvokeModelEnum clusterModel) {
        return clusterModel == ClusterInvokeModelEnum.FAIL_BACK;
    }
}
