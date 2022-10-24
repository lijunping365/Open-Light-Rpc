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
package com.saucesubfresh.rpc.core.information;

import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.enums.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lijunping 2021-10-30 13:42
 */
@ToString
@Getter
public class ServerInformation {
    /**
     * server address
     */
    private final String address;
    /**
     * server port
     */
    private final int port;
    /**
     * first online time
     */
    @Setter
    private long onlineTime;
    /**
     * this client status
     */
    @Setter
    private Status status;
    /**
     * node init weight
     */
    @Setter
    private int weight = 1;

    public ServerInformation(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Get formatted clientId
     *
     * @return The current client id
     */
    public String getServerId() {
        return String.format(CommonConstant.ADDRESS_PATTERN, this.address, this.port);
    }

    /**
     * Get new {@link ServerInformation} instance
     *
     * @param address client address
     * @param port    client port
     * @return {@link ServerInformation} instance
     */
    public static ServerInformation valueOf(String address, int port) {
        return new ServerInformation(address, port);
    }
}
