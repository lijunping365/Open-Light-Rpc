package com.saucesubfresh.rpc.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * server configuration
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.server")
public class ServerConfiguration {
    /**
     * The server port
     */
    private int serverPort = 5200;
    /**
     * Invoke retries
     */
    private int retryTimes = 3;
    /**
     * Time interval when retrying to invoke to Client, unit: millisecond
     *
     * @see java.util.concurrent.TimeUnit#MILLISECONDS
     */
    private long retryIntervalMilliSeconds = 1000;
    /**
     * The name of the service registered by the client to nacos or zookeeper
     * if you use nacos，You should be named like this: job-client-services
     * if you use zookeeper, You should be named like this: /JobClient
     */
    private String clientName;

}
