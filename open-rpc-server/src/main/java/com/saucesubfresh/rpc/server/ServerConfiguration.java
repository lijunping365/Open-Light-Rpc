package com.saucesubfresh.rpc.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务端配置
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.server")
public class ServerConfiguration {
    /**
     * The server address
     */
    private String serverAddress = "localhost";
    /**
     * The server port
     */
    private int serverPort = 5200;
    /**
     * The name of the service registered to the nacos or zookeeper
     * if you use nacos，You should be named like this: job-server-services
     * if you use zookeeper, You should be named like this: /JobServer
     */
    private String serverName;
}
