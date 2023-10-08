package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.exception.networking.requests.RequestFailException;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.exception.networking.authentication.AuthenticationException;
import fr.atlasworld.network.networking.NetworkUtilities;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Handles resources & exceptions
 */
@ChannelHandler.Sharable
public class ResourcesHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof AuthenticationException e) {
            AtlasNetwork.logger.error("Authentication failed for {}: ", ctx.channel().remoteAddress(), cause);

            PacketByteBuf buf = new PacketByteBufImpl(ctx.alloc().buffer())
                    .writeString(NetworkUtilities.StandardPackets.AUTHENTICATION_RESPONSE)
                    .writeBoolean(false)
                    .writeString(e.getNetworkFeedback());

            ctx.channel().writeAndFlush(buf).addListener(future -> {
                while (buf.isAccessible()) {
                    buf.release();
                }
            });

            return;
        }

        if (cause instanceof RequestFailException e) {
            NetworkClient client = ctx.channel().attr(NetworkUtilities.SESSION_ATTR_KEY).get();

            AtlasNetwork.logger.warn("Could not full-fill request for {}: ", client == null ? ctx.channel().remoteAddress() : client, e);

            PacketByteBuf buf = new PacketByteBufImpl(ctx.alloc().buffer())
                    .writeString(NetworkUtilities.StandardPackets.REQUEST_FAILURE)
                    .writeString(e.getNetworkFeedback());

            if (client == null) {
                ctx.channel().writeAndFlush(buf).addListener(future -> {
                    while (buf.isAccessible()) {
                        buf.release();
                    }
                });
            } else {
                client.sendPacket(buf).whenComplete(NetworkUtilities.SENT_PACKET_RESOURCES_CLEANUP);
            }

            return;
        }

        AtlasNetwork.logger.error("Something went wrong,", cause);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;
        while (buf.isAccessible()) {
            buf.release();
        }
    }
}
