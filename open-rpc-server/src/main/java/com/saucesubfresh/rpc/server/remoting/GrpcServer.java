package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lijunping on 2022/1/24
 */
@Slf4j
public class GrpcServer implements RpcServer, InitializingBean, DisposableBean {
    private static final ExecutorService SERVER_START_EXECUTOR = Executors.newSingleThreadExecutor();
    /**
     * The grpc server instance
     */
    private Server rpcServer;
    private final MessageHandler messageHandler;
    private final ServerConfiguration configuration;


    public GrpcServer(ServerConfiguration configuration, MessageHandler messageHandler){
        this.configuration = configuration;
        this.messageHandler = messageHandler;
    }

    /**
     * Build the grpc {@link Server} instance
     */
    private void buildServer() {
        this.rpcServer = ServerBuilder
                .forPort(configuration.getServerPort())
                .addService((BindableService) this.messageHandler)
                .build();
    }

    /**
     * Startup grpc {@link Server}
     */
    private void startup() {
        try {
            this.rpcServer.start();
            log.info("The Server bind port : {}, startup successfully.", configuration.getServerPort());
            this.rpcServer.awaitTermination();
        } catch (Exception e) {
            log.error("The Server startup failed.", e);
        }
    }

    /**
     * Shutdown grpc {@link Server}
     */
    private void shutdown() {
        try {
            log.info("The Server shutting down.");
            this.rpcServer.shutdown();
            long waitTime = 100;
            long timeConsuming = 0;
            while (!this.rpcServer.isShutdown()) {
                log.info("The Server stopping....，total time consuming：{}", timeConsuming);
                timeConsuming += waitTime;
                Thread.sleep(waitTime);
            }
            log.info("The Server stop successfully.");
        } catch (Exception e) {
            log.error("The Server shutdown failed.", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.buildServer();
        SERVER_START_EXECUTOR.execute(this::startup);
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
        SERVER_START_EXECUTOR.shutdown();
    }
}
