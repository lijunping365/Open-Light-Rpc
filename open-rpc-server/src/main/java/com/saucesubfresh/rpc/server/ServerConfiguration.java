package com.saucesubfresh.rpc.server;

import com.saucesubfresh.rpc.core.utils.internet.InternetAddressUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Related configuration items needed to build the client
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.client")
public class ServerConfiguration {
    /**
     * client host
     */
    private String clientAddress;
    /**
     * client port
     */
    private int clientPort = 5201;
    /**
     * Register the target server address
     */
    private String serverAddress = "localhost";
    /**
     * Register the target server port
     */
    private int serverPort = 5200;
    /**
     * The name of the service registered by the client to nacos or zookeeper
     * if you use nacos，You should be named like this: job-client-services
     * if you use zookeeper, You should be named like this: /JobClient
     */
    private String clientName;
    /**
     * Get local host
     *
     * @return local host
     */
    public String getClientAddress() {
        return InternetAddressUtils.getLocalIpByNetCard();
    }
}
