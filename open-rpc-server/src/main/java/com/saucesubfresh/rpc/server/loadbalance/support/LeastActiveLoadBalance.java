package com.saucesubfresh.rpc.server.loadbalance.support;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * 过滤活动调用最少的调用者数量，统计这些调用者的权重和数量。
 * 如果只有一个调用者，直接使用调用者；如果有多个invoker且权重不相同，则根据总权重随机；如果有多个调用者且权重相同，则随机调用。
 * @author: 李俊平
 * @Date: 2022-02-04 08:53
 */
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    @Override
    protected ClientInformation doSelect(Message message, List<ClientInformation> invokers) {
        return null;
    }
}
