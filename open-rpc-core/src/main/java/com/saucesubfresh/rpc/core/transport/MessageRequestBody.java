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
     * ID of the server receiving the message
     */
    private String serverId;
    /**
     * The message subject of this consumption
     */
    private Message message;
}
