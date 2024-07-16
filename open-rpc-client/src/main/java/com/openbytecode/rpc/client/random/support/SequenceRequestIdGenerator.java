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
package com.openbytecode.rpc.client.random.support;

import com.openbytecode.rpc.client.random.RequestIdGenerator;
import com.openbytecode.rpc.core.utils.sequence.Sequence;

/**
 * @author lijunping on 2022/3/23
 */
public class SequenceRequestIdGenerator implements RequestIdGenerator {
    private static final long DATA_CENTER_ID = 1L;
    private final Sequence sequence;

    public SequenceRequestIdGenerator() {
        this.sequence = new Sequence(DATA_CENTER_ID);
    }

    @Override
    public String generate() {
        return String.valueOf(sequence.nextId());
    }
}
