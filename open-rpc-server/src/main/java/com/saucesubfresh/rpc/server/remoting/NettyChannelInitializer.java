package com.saucesubfresh.rpc.server.remoting;

import com.saucesubfresh.rpc.core.codec.MsgDecoder;
import com.saucesubfresh.rpc.core.codec.MsgEncoder;
import com.saucesubfresh.rpc.core.constants.CommonConstant;
import com.saucesubfresh.rpc.core.grpc.proto.MessageRequest;
import com.saucesubfresh.rpc.core.grpc.proto.MessageResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * @author lijunping on 2022/8/8
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyClientHandler clientHandler;

    public NettyChannelInitializer(NettyClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ByteBuf delimiter = Unpooled.copiedBuffer(CommonConstant.DELIMITER.getBytes());
        ChannelPipeline cp = channel.pipeline();
        cp.addLast(new DelimiterBasedFrameDecoder(CommonConstant.MAX_LENGTH, delimiter));
        cp.addLast(new MsgDecoder(MessageResponse.class));
        cp.addLast(new MsgEncoder(MessageRequest.class));
        cp.addLast(clientHandler);
    }
}
