package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * 这里处理所有netty事件。
 * @author: 李俊平
 * @Date: 2022-06-08 08:04
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponse response) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client caught exception: {}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //Send ping
            final Channel channel = ctx.channel();
            SocketAddress socketAddress = channel.remoteAddress();
            log.debug("Client send beat-ping to " + socketAddress);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
