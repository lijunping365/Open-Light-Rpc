package com.saucesubfresh.rpc.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * server configuration
 */
@Data
@ConfigurationProperties(prefix = "com.saucesubfresh.rpc.client")
public class ClientConfiguration {
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
     * The name of the service registered to the nacos or zookeeper
     * if you use nacos，You should be named like this: job-server-services
     * if you use zookeeper, You should be named like this: /JobServer
     */
    private String serverName;

}
