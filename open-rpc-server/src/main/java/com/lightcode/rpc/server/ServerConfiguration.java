package com.lightcode.rpc.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * server configuration
 */
@Data
@ConfigurationProperties(prefix = "org.open.job.server")
public class ServerConfiguration {
    /**
     * The server port
     */
    private int serverPort = 5200;
    /**
     * The cluster model, @See BroadcastClusterInvoker，FailbackClusterInvoker, NormalClusterInvoker
     */
    private String clusterModel = "failover";
    /**
     * The loadBalance model, @See ConsistentHashLoadBalance，RandomWeightedLoadBalance
     */
    private String loadBalance = "consistentHash";
    /**
     * The store model, @See MemoryInstanceStore, RedissonInstanceStore
     */
    private String store;
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
}
