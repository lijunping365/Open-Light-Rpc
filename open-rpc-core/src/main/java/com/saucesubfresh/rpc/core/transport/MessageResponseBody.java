package com.saucesubfresh.rpc.core.transport;

import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;

/**
 * The message response
 */
@Data
@Accessors(chain = true)
public class MessageResponseBody {
    /**
     * The Response status
     */
    private ResponseStatus status = ResponseStatus.SUCCESS;
    /**
     * Message request number processed
     */
    private String requestId;
    /**
     * The Response body
     */
    private byte[] body;
}
