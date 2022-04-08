package com.saucesubfresh.rpc.client.registry.support;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import com.saucesubfresh.rpc.client.registry.AbstractRegistryService;
import com.lightcode.rpc.core.constants.CommonConstant;
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

    public ZookeeperRegistryService(ClientConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean doRegister(String serverAddress, int serverPort) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("clientIp", this.configuration.getClientAddress());
        metadata.put("clientPort", String.valueOf(this.configuration.getClientPort()));
        if (!zkClient.exists(CommonConstant.CLIENT_ROOT_PATH)) {
            zkClient.createPersistent(CommonConstant.CLIENT_ROOT_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        String clientInfo = String.format(CommonConstant.ADDRESS_PATTERN, configuration.getClientAddress(), configuration.getClientPort());
        String clientPath = CommonConstant.CLIENT_ROOT_PATH + CommonConstant.Symbol.SLASH + clientInfo;
        if (!zkClient.exists(clientPath)) {
            zkClient.createEphemeral(clientPath, metadata, ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        log.info("Current client registered to zookeeper server successfully.");
        return true;
    }

    @Override
    public boolean deRegister(String clientAddress, int clientPort) {
        String clientInfo = String.format(CommonConstant.ADDRESS_PATTERN, configuration.getClientAddress(), configuration.getClientPort());
        String clientPath = CommonConstant.CLIENT_ROOT_PATH + CommonConstant.Symbol.SLASH + clientInfo;
        this.zkClient.delete(clientPath);
        return true;
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
            log.warn("Failed to close zookeeper client " + configuration.getClientAddress() + ", cause: " + e.getMessage(), e);
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
