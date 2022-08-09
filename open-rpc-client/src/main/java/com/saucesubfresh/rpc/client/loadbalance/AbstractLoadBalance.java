package com.saucesubfresh.rpc.client.loadbalance;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: 李俊平
 * @Date: 2022-02-01 10:07
 */
public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public ServerInformation select(Message message, List<ServerInformation> invokers) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(message, invokers);
    }

    protected abstract ServerInformation doSelect(Message message, List<ServerInformation> invokers);
}
