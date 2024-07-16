/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openbytecode.rpc.client.remoting;

import com.openbytecode.rpc.core.transport.MessageResponseBody;

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
