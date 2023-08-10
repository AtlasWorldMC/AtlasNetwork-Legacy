package fr.atlasworld.network.old_networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.old_networking.session.SessionManager;
import fr.atlasworld.network.old_networking.packet.NetworkPacket;
import fr.atlasworld.network.old_networking.packet.PacketManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class PacketExecutor extends ChannelInboundHandlerAdapter {
    private final PacketManager packetManager;
    private final SessionManager sessionManager;

    public PacketExecutor(PacketManager packetManager, SessionManager sessionManager) {
        this.packetManager = packetManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;

        String packetId = buf.readString();
        NetworkPacket packet = this.packetManager.getPacket(packetId);

        if (packet == null) {
            AtlasNetwork.logger.warn("Received unknown packet from {}: {}", ctx.channel().remoteAddress(), packetId);

            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.UNKNOWN_PACKET);

            ctx.channel().writeAndFlush(response.getParent()).addListener(future -> {
                //Clean up
                buf.release();
                response.release();
            });

            return;
        }

        packet.execute(this.sessionManager.getSession(ctx.channel()), buf);
    }
}
