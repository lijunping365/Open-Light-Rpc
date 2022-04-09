package com.saucesubfresh.rpc.server.cluster;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;

/**
 * 对外提供的接口，提供给用户使用
 * @author lijunping on 2022/1/21
 */
public interface ClusterInvoker {

    /**
     * 给客户端发送消息
     * @param message
     * @throws RpcException
     */
    void invoke(Message message) throws RpcException;
}
