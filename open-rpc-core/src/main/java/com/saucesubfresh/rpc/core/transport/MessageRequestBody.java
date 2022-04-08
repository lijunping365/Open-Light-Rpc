package com.saucesubfresh.rpc.core.transport;

import com.saucesubfresh.rpc.core.Message;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The message request
 */
@Data
@Accessors(chain = true)
public class MessageRequestBody {
    /**
     * Unique number of message request
     */
    private String requestId;
    /**
     * ID of the client receiving the message
     */
    private String clientId;
    /**
     * The message subject of this consumption
     */
    private Message message;
}
