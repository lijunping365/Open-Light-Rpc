package com.lightcode.rpc.server.remoting;


import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.exception.RpcException;
import com.lightcode.rpc.core.information.ClientInformation;

/**
 * @author lijunping on 2022/2/16
 */
public interface RemotingInvoker {

    /**
     * 该接口是给系统调用的
     * @param message
     * @param clientInformation
     * @throws RpcException
     */
    void invoke(Message message, ClientInformation clientInformation) throws RpcException;
}
