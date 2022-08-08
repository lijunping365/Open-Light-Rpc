package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.client.ClientConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lijunping on 2022/6/8
 */
@Slf4j
public class NettyClient implements InitializingBean, DisposableBean {

    private static final ExecutorService RPC_JOB_EXECUTOR = Executors.newFixedThreadPool(1);

    public final ClientConfiguration configuration;

    public NettyClient(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public void startup(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .handler(new LoggingHandler(LogLevel.INFO))
                     .option(ChannelOption.SO_BACKLOG, 1024)
                     // 保持长连接
                     .childOption(ChannelOption.SO_KEEPALIVE, true)
                     // 处理网络io事件，如记录日志、对消息编解码等
                     .childHandler(new NettyChannelInitializer());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                bossGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
                workerGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
            }));
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("netty server start failure");
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int serverPort = configuration.getServerPort();
        RPC_JOB_EXECUTOR.execute(()->startup(serverPort));
    }

    @Override
    public void destroy() throws Exception {
        RPC_JOB_EXECUTOR.shutdown();
    }
}
