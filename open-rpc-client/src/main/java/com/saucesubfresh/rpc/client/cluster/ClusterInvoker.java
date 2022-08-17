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
     * 给服务端发送消息
     * @param namespace 应用名称
     * @param message 消息体
     * @throws RpcException
     */
    MessageResponseBody invoke(String namespace, Message message) throws RpcException;
}
