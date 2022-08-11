package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.server.ServerConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
public class NettyServer implements InitializingBean, DisposableBean {

    private static final ExecutorService RPC_JOB_EXECUTOR = Executors.newFixedThreadPool(1);

    private final ServerConfiguration configuration;
    private final ChannelInitializer<SocketChannel> channelInitializer;

    public NettyServer(ServerConfiguration configuration,
                       ChannelInitializer<SocketChannel> channelInitializer) {
        this.configuration = configuration;
        this.channelInitializer = channelInitializer;
    }

    public void startup(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .handler(new LoggingHandler(LogLevel.INFO))
                     // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                     .childOption(ChannelOption.TCP_NODELAY, true)
                     //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                     .option(ChannelOption.SO_BACKLOG, 128)
                     // 保持长连接, 是否开启 TCP 底层心跳机制
                     .childOption(ChannelOption.SO_KEEPALIVE, true)
                     // 当客户端第一次进行请求的时候才会进行初始化, 处理网络io事件，如记录日志、对消息编解码等
                     .childHandler(channelInitializer);
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
