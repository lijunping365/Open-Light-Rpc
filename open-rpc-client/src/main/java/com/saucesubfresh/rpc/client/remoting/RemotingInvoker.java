package com.saucesubfresh.rpc.client.remoting;


import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;

/**
 * @author lijunping on 2022/2/16
 */
public interface RemotingInvoker {

    /**
     * 该接口是给系统调用的
     * @param message
     * @param serverInformation
     * @throws RpcException
     */
    MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException;
}
