package com.saucesubfresh.rpc.server.namespace;

import java.util.Collections;
import java.util.List;

/**
 * @author lijunping on 2022/6/10
 */
public class DefaultNamespaceService implements NamespaceService{

    @Override
    public List<String> loadNamespace() {
        return Collections.emptyList();
    }
}
