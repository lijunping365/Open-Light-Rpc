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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lijunping on 2023/09/17
 */
public abstract class AbstractRemotingClient implements RemotingClient, InitializingBean, DisposableBean {
    private static final ExecutorService SERVER_START_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public void afterPropertiesSet() throws Exception {
        SERVER_START_EXECUTOR.execute(this::start);
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
        SERVER_START_EXECUTOR.shutdown();
    }
}
