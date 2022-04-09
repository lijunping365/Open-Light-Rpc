package com.saucesubfresh.rpc.server.random.support;

import com.saucesubfresh.rpc.core.utils.sequence.Sequence;
import com.saucesubfresh.rpc.server.random.RequestIdGenerator;

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
