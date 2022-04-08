package com.saucesubfresh.rpc.client.process;

import com.lightcode.rpc.core.Message;

/**
 * @author lijunping on 2022/1/19
 */
public interface MessageProcess {

    /**
     * 处理消息
     * @param message 消息
     */
    boolean process(Message message);
}
