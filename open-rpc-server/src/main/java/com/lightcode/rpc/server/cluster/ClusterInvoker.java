package com.lightcode.rpc.server.cluster;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.server.enums.ClusterInvokeModelEnum;

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

    /**
     * 是否支持该调用模式
     * @param clusterModel
     * @return
     */
    boolean support(ClusterInvokeModelEnum clusterModel);
}
