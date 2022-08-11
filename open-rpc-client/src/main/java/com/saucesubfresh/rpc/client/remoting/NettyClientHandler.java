package com.saucesubfresh.rpc.client.remoting;

import com.saucesubfresh.rpc.core.Message;
import com.saucesubfresh.rpc.core.enums.PacketType;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import com.saucesubfresh.rpc.core.transport.MessageRequestBody;
import com.saucesubfresh.rpc.core.utils.json.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 这里处理所有netty事件。
 * @author: 李俊平
 * @Date: 2022-06-08 08:04
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponse response) throws Exception {
        log.info("收到消息 {}", response);
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
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("Client send beat-ping to [{}]", ctx.channel().remoteAddress());
                Message message = new Message();
                message.setCommand(PacketType.PING);
                MessageRequestBody requestBody = new MessageRequestBody().setMessage(message);
                String requestJsonBody = JSON.toJSON(requestBody);
                MessageRequest messageRequest = MessageRequest.newBuilder().setBody(requestJsonBody).build();
                ctx.writeAndFlush(messageRequest);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
