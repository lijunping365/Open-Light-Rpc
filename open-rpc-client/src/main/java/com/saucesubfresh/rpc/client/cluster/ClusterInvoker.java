package com.saucesubfresh.rpc.client.cluster;


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
     * @param message
     * @throws RpcException
     */
    MessageResponseBody invoke(Message message) throws RpcException;
}
