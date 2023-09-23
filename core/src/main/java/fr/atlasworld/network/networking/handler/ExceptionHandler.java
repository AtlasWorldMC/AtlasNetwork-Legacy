package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetworkOld;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles exception thrown on the socket handler pipeline
 */
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().equals("Connection reset")) {
            AtlasNetworkOld.logger.info("{} disconnected", ctx.channel().remoteAddress());
            return;
        }

        AtlasNetworkOld.logger.error("Something went wrong,", cause);
    }
}
