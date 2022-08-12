package com.saucesubfresh.rpc.server.registry.support;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.registry.AbstractRegistryService;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Register to Server in Zookeeper mode
 * @author lijunping on 2021/12/2
 */
@Slf4j
public class ZookeeperRegistryService extends AbstractRegistryService implements InitializingBean, DisposableBean, BeanFactoryAware {
    private BeanFactory beanFactory;
    private ZkClient zkClient;

    public ZookeeperRegistryService(ServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void doRegister(String serverAddress, int serverPort) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("serverIp", serverAddress);
        metadata.put("serverPort", String.valueOf(serverPort));
        if (!zkClient.exists(this.configuration.getServerName())) {
            zkClient.createPersistent(this.configuration.getServerName(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        String serverInfo = String.format(CommonConstant.ADDRESS_PATTERN, serverAddress, serverPort);
        String serverPath = this.configuration.getServerName() + CommonConstant.Symbol.SLASH + serverInfo;
        if (!zkClient.exists(serverPath)) {
            zkClient.createEphemeral(serverPath, metadata, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        log.info("Current server registered to zookeeper server successfully.");
    }

    @Override
    public void deRegister(String serverAddress, int serverPort) {
        String serverInfo = String.format(CommonConstant.ADDRESS_PATTERN, serverAddress, serverPort);
        String serverPath = this.configuration.getServerName() + CommonConstant.Symbol.SLASH + serverInfo;
        this.zkClient.delete(serverPath);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void destroy() {
        try {
            zkClient.close();
        } catch (Exception e) {
            log.warn("Failed to close zookeeper client, cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.zkClient = beanFactory.getBean(ZkClient.class);
        } catch (BeansException e) {
            log.warn("No ZkClient instance is provided, a new instance will be automatically created for use");
        }
        super.afterPropertiesSet();
    }
}
