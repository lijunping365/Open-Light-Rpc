package com.saucesubfresh.rpc.server.process;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/3/18
 */
@Slf4j
public class DefaultMessageProcess implements MessageProcess{

    @Override
    public byte[] process(Message message) {
        log.info("收到的消息是 {}", JSON.toJSON(message));
        return null;
    }
}
