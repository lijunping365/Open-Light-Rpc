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
package com.openbytecode.rpc.loadbalance;


import com.openbytecode.rpc.core.Message;
import com.openbytecode.rpc.core.information.ServerInformation;

import java.util.List;

/**
 * 过滤活动调用最少的调用者数量，统计这些调用者的权重和数量。
 * 如果只有一个调用者，直接使用调用者；如果有多个invoker且权重不相同，则根据总权重随机；如果有多个调用者且权重相同，则随机调用。
 *
 * @author lijunping 2022-02-04 08:53
 */
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    @Override
    protected ServerInformation doSelect(Message message, List<ServerInformation> invokers) {
        return null;
    }
}
