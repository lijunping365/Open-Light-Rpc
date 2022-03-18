package com.lightcode.rpc.client.process;

import com.lightcode.rpc.core.Message;
import com.lightcode.rpc.core.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lijunping on 2022/3/18
 */
@Slf4j
@Component
public class DefaultMessageProcess implements MessageProcess{

    @Override
    public boolean process(Message message) {
        log.info("收到的消息是 {}", JSON.toJSON(message));
        return true;
    }
}
