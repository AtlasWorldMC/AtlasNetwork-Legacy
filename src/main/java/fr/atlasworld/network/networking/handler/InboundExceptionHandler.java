package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.exceptions.request.RequestFailureException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.exceptions.AuthenticationException;
import fr.atlasworld.network.networking.security.encryption.exceptions.EncryptionException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Handles exception thrown on the socket handler pipeline
 */
@ChannelHandler.Sharable
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof AuthenticationException e) {
            PacketByteBuf packet = new PacketByteBuf(ctx.alloc().buffer())
                    .writeString("authentication")
                    .writeBoolean(false)
                    .writeString(e.getNetworkFeedback());

            ctx.channel().writeAndFlush(packet);

            AtlasNetwork.logger.warn("{}: {}", ctx.channel().remoteAddress(), e.getMessage());
            AtlasNetwork.logger.debug("{}:", ctx.channel().remoteAddress(), e);
            return;
        }

        if (cause instanceof RequestFailureException e) {
            PacketByteBuf packet = new PacketByteBuf(ctx.alloc().buffer())
                    .writeString("request_failure")
                    .writeString(e.getNetworkFeedback());

            ctx.channel().writeAndFlush(packet);

            AtlasNetwork.logger.warn("{}: {}", ctx.channel().remoteAddress(), e.getMessage());
            AtlasNetwork.logger.debug("{}: ", ctx.channel().remoteAddress(), e);
            return;
        }

        if (cause instanceof EncryptionException e) {
            PacketByteBuf packet = new PacketByteBuf(ctx.alloc().buffer())
                    .writeString("handshake")
                    .writeBoolean(false)
                    .writeString(e.getNetworkFeedback());

            ctx.channel().writeAndFlush(packet);

            AtlasNetwork.logger.error("Ooops! Something went wrong while establishing encryption with {}!", ctx.channel().remoteAddress(), e);
            ctx.channel().disconnect();
            return;
        }
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;

        if (buf.refCnt() > 0) {
            buf.releaseFully();
            AtlasNetwork.logger.debug("Released ByteBuf '{}'", buf);
        }
    }
}
