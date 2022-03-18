package com.lightcode.rpc.server.loadbalance;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;
import com.lightcode.rpc.server.enums.LoadBalanceModelEnum;

import java.util.List;

/**
 * @author lijunping on 2022/1/20
 */
public interface LoadBalance {

    /**
     * Lookup a {@link ClientInformation}
     *
     *
     * @param message
     * @param clients {@link ClientInformation} list
     * @return load-balanced client
     * @throws org.open.job.core.exception.RpcException
     */
    ClientInformation select(Message message, List<ClientInformation> clients) throws RpcException;

    /**
     * Is Support LoadBalance
     * @param loadBalanceModelEnum
     * @return true support, false is not support
     */
    boolean support(LoadBalanceModelEnum loadBalanceModelEnum);
}
