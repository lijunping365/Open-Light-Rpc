package com.saucesubfresh.rpc.server.loadbalance;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ClientInformation;

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
     * @throws RpcException
     */
    ClientInformation select(Message message, List<ClientInformation> clients) throws RpcException;
}
