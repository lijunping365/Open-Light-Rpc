package com.saucesubfresh.rpc.core;

import com.saucesubfresh.rpc.core.enums.PacketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Message entity
 */
@Data
@NoArgsConstructor
public class Message implements Serializable {
    private static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
    private PacketType command = PacketType.MESSAGE;
    private String msgId;
    private String bodyEncoding;
    private byte[] body;

    public Message(byte[] body) {
        this(body, DEFAULT_ENCODING);
    }

    public Message(byte[] body, String bodyEncoding) {
        this.bodyEncoding = bodyEncoding;
        this.body = body;
    }
}