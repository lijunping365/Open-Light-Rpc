package com.saucesubfresh.rpc.client.callback;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.information.ServerInformation;

@FunctionalInterface
public interface CallCallback {

    void onCall(Message message, ServerInformation serverInformation);
}
