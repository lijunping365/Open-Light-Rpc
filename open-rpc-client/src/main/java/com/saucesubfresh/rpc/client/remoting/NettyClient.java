package com.saucesubfresh.rpc.client.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 客户端
 * @author: 李俊平
 * @Date: 2022-06-08 07:25
 */
@Slf4j
public class NettyClient {

    private final Bootstrap bootstrap;

    private final NettyChannelInitializer initializer;

    public NettyClient(NettyChannelInitializer initializer) {
        this.initializer = initializer;
        this.bootstrap = initBootstrap();
    }

    public Bootstrap initBootstrap(){
        EventLoopGroup group = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(initializer);
        return bootstrap;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }
}
