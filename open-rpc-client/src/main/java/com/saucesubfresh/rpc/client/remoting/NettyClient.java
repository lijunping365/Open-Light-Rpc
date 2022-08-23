package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.codec.MsgDecoder;
import com.saucesubfresh.rpc.core.codec.MsgEncoder;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
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
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端
 * @author: 李俊平
 * @Date: 2022-06-08 07:25
 */
@Slf4j
public class NettyClient implements RpcClient{
    private final Bootstrap bootstrap;

    public NettyClient() {
        this.bootstrap = initBootstrap();
    }

    public Bootstrap initBootstrap(){
        EventLoopGroup group = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .channel(NioSocketChannel.class)
                 .handler(new LoggingHandler(LogLevel.INFO))
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChildChannelHandler());
        return bootstrap;
    }

    /**
     * handler
     */
    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) {
            ByteBuf delimiter = Unpooled.copiedBuffer(CommonConstant.DELIMITER.getBytes());
            ChannelPipeline cp = channel.pipeline();
            // If no data is sent to the server within 15 seconds, a heartbeat request is sent
            cp.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
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
