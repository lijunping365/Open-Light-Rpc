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
package com.openbytecode.rpc.server.process;

import com.openbytecode.rpc.core.Message;
import com.openbytecode.rpc.core.transport.MessageResponseBody;
import com.openbytecode.rpc.core.utils.json.JSON;
import com.openbytecode.rpc.server.callback.ResponseWriter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/3/18
 */
@Slf4j
public class DefaultMessageProcess extends AbstractMessageProcess{

    @Override
    protected void doProcess(Message message, MessageResponseBody responseBody, ResponseWriter responseWriter) {
        log.info("收到的消息是 {}", JSON.toJSON(message));
    }
}
