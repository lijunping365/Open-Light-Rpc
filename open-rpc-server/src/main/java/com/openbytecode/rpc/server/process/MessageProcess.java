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

import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.server.callback.ResponseWriter;

/**
 * @author lijunping on 2022/1/19
 */
public interface MessageProcess {

    /**
     * 处理消息
     * @param request
     * @param callback
     */
    void process(MessageRequest request, ResponseWriter callback);
}
