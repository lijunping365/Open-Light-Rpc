package com.saucesubfresh.rpc.server.discovery.support;

import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.information.ClientInformation;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.discovery.AbstractServiceDiscovery;
import com.saucesubfresh.rpc.server.remoting.RemotingInvoker;
import com.saucesubfresh.rpc.server.store.InstanceStore;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijunping on 2021/12/3
 */
@Slf4j
public class ZookeeperRegistryService extends AbstractServiceDiscovery implements InitializingBean, DisposableBean{
    private final ZkClient zkClient;

    public ZookeeperRegistryService(ZkClient zkClient, RemotingInvoker remotingInvoker, InstanceStore instanceStore, ServerConfiguration configuration){
        super(remotingInvoker, instanceStore, configuration);
        this.zkClient = zkClient;
    }

    /**
     * 使用zk事件监听，如果服务发生宕机情况，重新读取新的节点
     */
    private void subscribe(){
        zkClient.subscribeChildChanges(configuration.getClientName(), new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                log.info("zookeeper 父节点 {} 下的子节点列表 {}", parentPath, currentChilds);
                final List<ClientInformation> collect = currentChilds.stream().map(e -> {
                    final String[] split = StringUtils.split(e, CommonConstant.Symbol.MH);
                    return ClientInformation.valueOf(split[0], Integer.parseInt(split[1]));
                }).collect(Collectors.toList());
                updateCache(collect);
                log.info("register instance successfully {}", collect);
            }
        });
    }

    @Override
    protected List<ClientInformation> doLookup() {
        List<String> children = zkClient.getChildren(configuration.getClientName());
        log.info("查询到的子节点有 {}", children);
        return children.stream().map(e->{
            final String[] split = StringUtils.split(e, CommonConstant.Symbol.MH);
            return ClientInformation.valueOf(split[0], Integer.parseInt(split[1]));
        }).collect(Collectors.toList());
    }

    @Override
    public void destroy() throws Exception {
        this.zkClient.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.subscribe();
    }
}
