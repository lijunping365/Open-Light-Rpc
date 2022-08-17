package com.saucesubfresh.rpc.client.namespace;

import java.util.Collections;
import java.util.List;

/**
 * @author lijunping on 2022/8/17
 */
public class DefaultNamespaceService implements NamespaceService{

    @Override
    public List<String> loadNamespace() {
        return Collections.emptyList();
    }
}
