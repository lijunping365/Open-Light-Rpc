/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.codec.MsgDecoder;
import com.saucesubfresh.rpc.core.codec.MsgEncoder;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.server.ServerConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lijunping on 2022/6/8
 */
@Slf4j
public class NettyServer extends AbstractRemotingServer {
    private final MessageHandler messageHandler;
    private final ServerConfiguration configuration;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup selectorGroup;

    public NettyServer(MessageHandler messageHandler, ServerConfiguration configuration) {
        this.messageHandler = messageHandler;
        this.configuration = configuration;
        this.serverBootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup();
        this.selectorGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() {
        serverBootstrap.group(bossGroup, selectorGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
            .childOption(ChannelOption.TCP_NODELAY, true)
            //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
            .option(ChannelOption.SO_BACKLOG, 128)
            // 保持长连接, 是否开启 TCP 底层心跳机制
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            // 当客户端第一次进行请求的时候才会进行初始化, 处理网络io事件，如记录日志、对消息编解码等
            .childHandler(new ChildChannelHandler());
        try{
            //绑定端口，同步等待成功
            int serverPort = configuration.getServerPort();
            ChannelFuture future = serverBootstrap.bind(serverPort).sync();
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("netty server start failure");
        }
    }

    @Override
    public void shutdown() {
        try {
            bossGroup.shutdownGracefully();
            selectorGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("NettyServer shutdown exception, ", e);
        }
    }

    /**
     * handler
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) {
            ByteBuf delimiter = Unpooled.copiedBuffer(CommonConstant.DELIMITER.getBytes());
            ChannelPipeline cp = channel.pipeline();
            cp.addLast(new DelimiterBasedFrameDecoder(CommonConstant.MAX_LENGTH, delimiter));
            cp.addLast(new MsgDecoder(MessageRequest.class));
            cp.addLast(new MsgEncoder(MessageResponse.class));
            cp.addLast((ChannelHandler) messageHandler);
        }
    }
}