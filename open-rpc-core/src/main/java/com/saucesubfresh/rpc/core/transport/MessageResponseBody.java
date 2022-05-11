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
     * 1.When you use a similar code to get byte[], byte[] bytes = "abc".getBytes(Charset.defaultCharset());
     * Then when you want to get String, You should specify Charset.
     * 2.When you get byte[] by ProtostuffIOUtil,
     * Then you don’t need to specify Charset when you decode byte[].
     */
    private static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
    /**
     * The Response status
     */
    private ResponseStatus status = ResponseStatus.SUCCESS;
    /**
     * The Response body
     */
    private byte[] body;
}
