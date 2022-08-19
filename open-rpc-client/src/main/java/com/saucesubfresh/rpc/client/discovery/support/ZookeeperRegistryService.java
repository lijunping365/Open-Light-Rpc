package com.saucesubfresh.rpc.client.discovery.support;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.discovery.AbstractServiceDiscovery;
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

    public ZookeeperRegistryService(ZkClient zkClient, InstanceStore instanceStore, ClientConfiguration configuration){
        super(instanceStore, configuration);
        this.zkClient = zkClient;
    }

    /**
     * 使用zk事件监听，如果服务发生宕机情况，重新读取新的节点
     */
    private void subscribe(){
        zkClient.subscribeChildChanges(configuration.getServerName(), new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> children) throws Exception {
                List<ServerInformation> onlineServers = convert(children);
                log.info("zookeeper parentPath {}, current online instance {}", parentPath, onlineServers);
                updateCache(onlineServers);
            }
        });
    }

    @Override
    protected List<ServerInformation> doLookup() {
        List<ServerInformation> onlineServers = new ArrayList<>();
        try {
            List<String> children = zkClient.getChildren(configuration.getServerName());
            onlineServers = convert(children);
            log.info("lookup online instance {}", onlineServers);
        }catch (Exception e){
            log.error("lookup instance failed {}", e.getMessage());
        }
        return onlineServers;
    }

    @Override
    public void destroy() throws Exception {
        this.zkClient.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.subscribe();
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
