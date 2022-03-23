package com.lightcode.rpc.client;

import com.lightcode.rpc.core.utils.internet.InternetAddressUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Related configuration items needed to build the client
 */
@Data
@ConfigurationProperties(prefix = "com.lightcode.rpc.client")
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
     * Get local host
     *
     * @return local host
     */
    public String getClientAddress() {
        return InternetAddressUtils.getLocalIpByNetCard();
    }
}
