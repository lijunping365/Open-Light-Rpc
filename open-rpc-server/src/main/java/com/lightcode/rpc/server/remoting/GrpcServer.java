package com.lightcode.rpc.server.remoting;

import com.lightcode.rpc.server.ServerConfiguration;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端
 * 默认使用 gRpc 通信，如果有兴趣可更改为其他通信方式，例如 netty
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcServer implements InitializingBean, DisposableBean {

    private static final ExecutorService RPC_JOB_EXECUTOR = Executors.newFixedThreadPool(1);

    private final ServerConfiguration configuration;
    /**
     * The grpc server instance
     */
    private Server rpcServer;

    public GrpcServer(ServerConfiguration configuration){
        this.configuration = configuration;
    }

    /**
     * Build the grpc {@link Server} instance
     */
    private void buildServer() {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(this.configuration.getServerPort());
        this.rpcServer = serverBuilder.build();
    }

    /**
     * Startup grpc {@link Server}
     */
    public void startup() {
        try {
            this.rpcServer.start();
            log.info("JobServer bind port : {}, startup successfully.", configuration.getServerPort());
            this.rpcServer.awaitTermination();
        } catch (Exception e) {
            log.error("JobServer startup failed.", e);
        }
    }

    /**
     * Shutdown grpc {@link Server}
     */
    public void shutdown() {
        try {
            log.info("JobServer shutting down.");
            this.rpcServer.shutdown();
            long waitTime = 100;
            long timeConsuming = 0;
            while (!this.rpcServer.isShutdown()) {
                log.info("JobServer stopping....，total time consuming：{}", timeConsuming);
                timeConsuming += waitTime;
                Thread.sleep(waitTime);
            }
            log.info("JobServer stop successfully.");
        } catch (Exception e) {
            log.error("JobServer shutdown failed.", e);
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
