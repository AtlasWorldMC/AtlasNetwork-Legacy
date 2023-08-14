package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().equals("Connection reset")) {
            AtlasNetwork.logger.info("{} disconnected", ctx.channel().remoteAddress());
            return;
        }

        AtlasNetwork.logger.error("Something went wrong,", cause);
    }
}
