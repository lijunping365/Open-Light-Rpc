package com.saucesubfresh.rpc.client.discovery.support;

import com.saucesubfresh.rpc.client.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.client.namespace.NamespaceService;
import com.saucesubfresh.rpc.client.store.InstanceStore;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.enums.Status;
import com.saucesubfresh.rpc.core.information.ServerInformation;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
            public void handleChildChange(String parentPath, List<String> children) throws Exception {
                List<ServerInformation> onlineServers = convert(children);
                log.info("zookeeper parentPath {}, current online instance {}", parentPath, onlineServers);
                updateCache(namespace, onlineServers);
            }
        }));
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        List<ServerInformation> onlineServers = new ArrayList<>();
        try {
            List<String> children = zkClient.getChildren(namespace);
            onlineServers = convert(children);
            log.info("lookup online instance {}", onlineServers);
        }catch (Exception e){
            log.error("lookup instance failed {}", e.getMessage());
        }
        return onlineServers;
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

    @Override
    public void destroy() throws Exception {
        this.zkClient.close();
    }

    private List<ServerInformation> convert(List<String> instances){
        if (CollectionUtils.isEmpty(instances)){
            return new ArrayList<>();
        }
        return instances.stream().map(instance -> {
            long currentTime = System.currentTimeMillis();
            String[] split = StringUtils.split(instance, CommonConstant.Symbol.MH);
            ServerInformation serverInfo = ServerInformation.valueOf(split[0], Integer.parseInt(split[1]));
            serverInfo.setStatus(Status.ON_LINE);
            serverInfo.setOnlineTime(currentTime);
            return serverInfo;
        }).collect(Collectors.toList());
    }
}
