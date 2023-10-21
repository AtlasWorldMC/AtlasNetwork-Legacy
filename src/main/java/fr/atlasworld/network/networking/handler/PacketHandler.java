package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Packet handler, prepares data for the packets, before sending it to the PacketManager that will execute them
 * @see PacketManager
 */
@ChannelHandler.Sharable
public class PacketHandler extends ChannelInboundHandlerAdapter {
    private final PacketManager packetManager;
    private final SessionManager sessionManager;

    public PacketHandler(PacketManager packetManager, SessionManager sessionManager) {
        this.packetManager = packetManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;
        UUID connectionId = (UUID) ctx.channel().attr(AttributeKey.valueOf("conn_id")).get();
        NetworkClient session = (NetworkClient) this.sessionManager.getSession(connectionId);
        this.packetManager.execute(buf.readString(), session, buf);
    }
}
