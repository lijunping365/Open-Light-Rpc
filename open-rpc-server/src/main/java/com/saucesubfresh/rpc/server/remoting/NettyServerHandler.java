package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 这里处理所有netty事件。
 * @author: 李俊平
 * @Date: 2022-06-08 08:04
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponse response) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("some thing is error , " + cause.getMessage());
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
}
