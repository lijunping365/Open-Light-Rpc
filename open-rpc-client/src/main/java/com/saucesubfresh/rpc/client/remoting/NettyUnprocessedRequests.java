package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.transport.MessageResponseBody;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lijunping on 2022/8/11
 */
public class NettyUnprocessedRequests {

    private static final Map<String, CompletableFuture<MessageResponseBody>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>(16);

    public static void put(String requestId, CompletableFuture<MessageResponseBody> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public static void complete(MessageResponseBody responseBody) {
        CompletableFuture<MessageResponseBody> future = UNPROCESSED_RESPONSE_FUTURES.remove(responseBody.getRequestId());
        if (null != future) {
            future.complete(responseBody);
        } else {
            throw new IllegalStateException();
        }
    }
}
