package com.saucesubfresh.rpc.client.namespace;

import java.util.List;

/**
 * @author lijunping on 2022/8/17
 */
public interface NamespaceService {

    /**
     * load namespaces
     * @return
     */
    List<String> loadNamespace();
}
