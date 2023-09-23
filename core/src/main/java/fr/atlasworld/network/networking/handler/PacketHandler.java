package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

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
        NetworkClient session = this.sessionManager.getSession(ctx.channel());
        this.packetManager.execute(buf.readString(), session, buf);
    }
}
