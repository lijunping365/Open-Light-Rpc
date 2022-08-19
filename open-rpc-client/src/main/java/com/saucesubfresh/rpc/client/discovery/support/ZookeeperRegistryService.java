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
                log.info("zookeeper 父节点 {} 下的子节点列表 {}", parentPath, children);
                List<ServerInformation> collect = convert(children);
                updateCache(namespace, collect);
                log.info("register instance successfully {}", collect);
            }
        }));
    }

    @Override
    protected List<ServerInformation> doLookup(String namespace) {
        List<String> children = zkClient.getChildren(namespace);
        log.info("查询到的子节点有 {}", children);
        return convert(children);
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
