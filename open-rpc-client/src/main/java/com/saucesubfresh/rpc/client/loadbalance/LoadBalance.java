package com.saucesubfresh.rpc.client.loadbalance;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * @author lijunping on 2022/1/20
 */
public interface LoadBalance {

    /**
     * Lookup a {@link ServerInformation}
     *
     *
     * @param message
     * @param clients {@link ServerInformation} list
     * @return load-balanced client
     * @throws RpcException
     */
    ServerInformation select(Message message, List<ServerInformation> clients) throws RpcException;
}
