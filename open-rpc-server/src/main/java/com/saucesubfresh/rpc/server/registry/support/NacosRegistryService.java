package com.saucesubfresh.rpc.server.registry.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import com.saucesubfresh.rpc.server.registry.AbstractRegistryService;
import com.saucesubfresh.rpc.core.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Register to Server in Nacos mode
 * @author: 李俊平
 * @Date: 2021-10-31 14:38
 */
@Slf4j
public class NacosRegistryService extends AbstractRegistryService implements InitializingBean, DisposableBean, BeanFactoryAware {
    private BeanFactory beanFactory;
    private NamingService namingService;

    public NacosRegistryService(ServerConfiguration configuration) {
        super(configuration);
    }
    /**
     * Register client to nacos server
     * <p>
     * If there is an instance of {@link NamingService} in the running project,
     * use it directly, otherwise create a new instance
     *
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    @Override
    public void doRegister(String serverAddress, int serverPort) {
        try {
            Instance instance = new Instance();
            instance.setIp(serverAddress);
            instance.setPort(serverPort);
            Map<String, String> metadata = new HashMap<>();
            instance.setMetadata(metadata);
            this.namingService.registerInstance(this.configuration.getClientName(), instance);
            log.info("Current client registered to nacos server successfully.");
        } catch (Exception e) {
            log.error("register instance failed {}", e.getMessage());
            throw new RpcException(e.getMessage());
        }
    }

    @Override
    public void deRegister(String clientAddress, int clientPort) {
        try {
            this.namingService.deregisterInstance(this.configuration.getClientName(), clientAddress, clientPort);
        } catch (NacosException e) {
            log.error("deRegister instance failed {}", e.getMessage());
            throw new RpcException(e.getMessage());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void destroy() throws Exception {
        this.namingService.shutDown();
        log.info("The client is successfully offline from the nacos server.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.namingService = this.beanFactory.getBean(NamingService.class);
        } catch (BeansException e) {
            log.warn("No NamingService instance is provided, a new instance will be automatically created for use");
        }
        super.afterPropertiesSet();
    }
}
