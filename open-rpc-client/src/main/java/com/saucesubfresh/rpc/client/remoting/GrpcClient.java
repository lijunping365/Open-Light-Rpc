package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端
 * 默认使用 gRpc 通信，如果有兴趣可更改为其他通信方式，例如 netty
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcClient implements InitializingBean, DisposableBean {
    private static final ExecutorService RPC_JOB_EXECUTOR = Executors.newFixedThreadPool(1);
    /**
     * The grpc server instance
     */
    private Server rpcServer;
    public final ClientConfiguration configuration;
    private final BindableService bindableService;

    public GrpcClient(ClientConfiguration configuration, GrpcMessageHandler bindableService){
        this.configuration = configuration;
        this.bindableService = bindableService;
    }

    /**
     * Build the grpc {@link Server} instance
     */
    private void buildServer() {
        this.rpcServer = ServerBuilder
                .forPort(configuration.getClientPort())
                .addService(this.bindableService)
                .build();
    }

    /**
     * Startup grpc {@link Server}
     */
    public void startup() {
        try {
            this.rpcServer.start();
            log.info("Job Client bind port : {}, startup successfully.", configuration.getClientPort());
            this.rpcServer.awaitTermination();
        } catch (Exception e) {
            log.error("Job Client startup failed.", e);
        }
    }

    /**
     * Shutdown grpc {@link Server}
     */
    public void shutdown() {
        try {
            log.info("Job Client shutting down.");
            this.rpcServer.shutdown();
            long waitTime = 100;
            long timeConsuming = 0;
            while (!this.rpcServer.isShutdown()) {
                log.info("Job Client stopping....，total time consuming：{}", timeConsuming);
                timeConsuming += waitTime;
                Thread.sleep(waitTime);
            }
            log.info("Job Client stop successfully.");
        } catch (Exception e) {
            log.error("Job Client shutdown failed.", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.buildServer();
        RPC_JOB_EXECUTOR.execute(this::startup);
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
        RPC_JOB_EXECUTOR.shutdown();
    }
}
