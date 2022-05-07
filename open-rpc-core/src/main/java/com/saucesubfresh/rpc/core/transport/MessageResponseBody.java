package com.saucesubfresh.rpc.core.transport;

import com.saucesubfresh.rpc.core.Message;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The message response
 */
@Data
@Accessors(chain = true)
public class MessageResponseBody {
    /**
     * Message request number processed
     */
    private String requestId;
    /**
     * The Response body
     */
    private Message responseBody;
}
