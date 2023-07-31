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
package com.saucesubfresh.rpc.client.remoting;


import com.saucesubfresh.rpc.client.callback.CallCallback;
import com.saucesubfresh.rpc.client.callback.ResponseReader;
import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.exception.RpcException;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import com.saucesubfresh.rpc.core.transport.MessageResponseBody;

/**
 * @author lijunping on 2022/2/16
 */
public interface RemotingInvoker {

    /**
     * 同步调用
     * @param message
     * @param serverInformation
     * @throws RpcException
     */
    MessageResponseBody invoke(Message message, ServerInformation serverInformation) throws RpcException;

    /**
     * 异步调用
     * @param message
     * @param serverInformation
     * @throws RpcException
     */
    void invokeAsync(Message message, ServerInformation serverInformation, CallCallback callback) throws RpcException;
}
