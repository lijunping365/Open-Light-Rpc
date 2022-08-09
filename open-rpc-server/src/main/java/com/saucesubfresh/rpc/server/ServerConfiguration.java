package com.saucesubfresh.rpc.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Related configuration items needed to build the client
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.server")
public class ServerConfiguration {
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
}
