package com.lightcode.rpc.client;

import com.lightcode.rpc.core.internet.InternetAddressUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Related configuration items needed to build the client
 */
@Data
@ConfigurationProperties(prefix = "org.open.job.client")
public class ClientConfiguration {
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
     * The username of nacos
     */
    private String username = "nacos";
    /**
     * The password of nacos
     */
    private String password = "nacos";
    /**
     * The address of nacos | zookeeper
     */
    private String address = "127.0.0.1";
    /**
     * The port of nacos（8848） | zookeeper（2181）
     */
    private int port = 8848;
    /**
     * The connectionTimeout of zookeeper
     */
    private int connectionTimeout = 5000;
    /**
     * Get local host
     *
     * @return local host
     */
    public String getClientAddress() {
        return InternetAddressUtils.getLocalIpByNetCard();
    }
}
