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
package com.openbytecode.rpc.client.remoting;

import com.openbytecode.rpc.core.codec.MsgDecoder;
import com.openbytecode.rpc.core.codec.MsgEncoder;
import com.openbytecode.rpc.core.constants.CommonConstant;
import com.openbytecode.rpc.core.grpc.proto.MessageRequest;
import com.openbytecode.rpc.core.grpc.proto.MessageResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Netty 客户端
 *
 * @author lijunping 2022-06-08 07:25
 */
@Slf4j
public class NettyClient extends AbstractRemotingClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    public NettyClient() {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup(2);
    }

    @Override
    public void start() {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChildChannelHandler());
    }

    @Override
    public void shutdown() {
        try {
            group.shutdownGracefully();
        } catch (Exception e) {
            log.error("NettyServer shutdown exception, ", e);
        }

        ConcurrentMap<String, Channel> serverChannel = NettyClientChannelManager.getServerChannel();
        if (serverChannel.size() == 0) {
            return;
        }

        for (Map.Entry<String, Channel> item: serverChannel.entrySet()) {
            try {
                item.getValue().closeFuture();
                NettyClientChannelManager.removeChannel(item.getKey());
            }catch (Exception e){
                log.error("Channel close exception, ", e);
            }
        }
    }

    /**
     * handler
     */
    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) {
            ByteBuf delimiter = Unpooled.copiedBuffer(CommonConstant.DELIMITER.getBytes());
            ChannelPipeline cp = channel.pipeline();
            cp.addLast(new DelimiterBasedFrameDecoder(CommonConstant.MAX_LENGTH, delimiter));
            cp.addLast(new MsgDecoder(MessageResponse.class));
            cp.addLast(new MsgEncoder(MessageRequest.class));
            cp.addLast(new NettyClientHandler());
        }
    }

    /**
     * connect sync
     */
    public Channel connect(String address, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
        return channelFuture.channel();
    }

    /**
     * connect async
     */
    public Channel connectAsync(String address, int port) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("Connect to server [{}:{}] successful!", address, port);
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
