package com.saucesubfresh.rpc.client.callback;

import com.saucesubfresh.rpc.core.transport.MessageResponseBody;

public interface ResponseReader {

    /**
     * 响应数据
     * @return
     */
    void read(MessageResponseBody responseBody);
}
