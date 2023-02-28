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
package com.saucesubfresh.rpc.core.transport;

import com.saucesubfresh.rpc.core.enums.ResponseStatus;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The message response
 *
 * @author lijunping
 */
@Data
@Accessors(chain = true)
public class MessageResponseBody {
    /**
     * The Response body
     */
    private byte[] body;
    /**
     * The error msg，when status is ERROR
     */
    private String msg;
    /**
     * Message request number processed
     */
    private String requestId;
    /**
     * serverId of server address connect port
     */
    private String serverId;
    /**
     * The Response status
     */
    private ResponseStatus status = ResponseStatus.SUCCESS;
}
