package com.saucesubfresh.rpc.server.cluster;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;

/**
 * 对外提供的接口，提供给用户使用
 * @author lijunping on 2022/1/21
 */
public interface ClusterInvoker {

    /**
     * 给客户端发送消息
     * @param namespace
     * @param message
     * @return
     * @throws RpcException
     */
    MessageResponseBody invoke(String namespace, Message message) throws RpcException;
}
