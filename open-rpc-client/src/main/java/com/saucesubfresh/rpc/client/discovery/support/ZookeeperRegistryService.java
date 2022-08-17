package com.saucesubfresh.rpc.client.discovery.support;

import com.saucesubfresh.rpc.client.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.client.namespace.NamespaceService;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2021/12/3
 */
@Slf4j
public class ZookeeperRegistryService extends AbstractServiceDiscovery implements InitializingBean, DisposableBean{
    private final ZkClient zkClient;
    private final NamespaceService namespaceService;

    public ZookeeperRegistryService(ZkClient zkClient, InstanceStore instanceStore, NamespaceService namespaceService){
        super(instanceStore);
        this.zkClient = zkClient;
        this.namespaceService = namespaceService;
    }


    @Override
    public void subscribe(List<String> namespaces){
        if (CollectionUtils.isEmpty(namespaces)){
            return;
        }
        namespaces.forEach(namespace-> zkClient.subscribeChildChanges(namespace, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                log.info("zookeeper 父节点 {} 下的子节点列表 {}", parentPath, currentChilds);
                final List<ServerInformation> collect = currentChilds.stream().map(e -> {
                    final String[] split = StringUtils.split(e, CommonConstant.Symbol.MH);
                    return ServerInformation.valueOf(split[0], Integer.parseInt(split[1]));
                }).collect(Collectors.toList());
                updateCache(namespace, collect);
                log.info("register instance successfully {}", collect);
            }
        }));
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        List<String> children = zkClient.getChildren(namespace);
        log.info("查询到的子节点有 {}", children);
        return children.stream().map(e->{
            final String[] split = StringUtils.split(e, CommonConstant.Symbol.MH);
            return ServerInformation.valueOf(split[0], Integer.parseInt(split[1]));
        }).collect(Collectors.toList());
    }

    @Override
    public void destroy() throws Exception {
        this.zkClient.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            List<String> namespaces = namespaceService.loadNamespace();
            this.subscribe(namespaces);
        }catch (Exception e){
            log.error("load namespace failed: {}", e.getMessage());
        }
    }
}
