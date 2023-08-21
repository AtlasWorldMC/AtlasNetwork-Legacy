package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.session.ClientSession;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

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
        ClientSession session = this.sessionManager.getSession(ctx.channel());
        this.packetManager.execute(buf.readString(), session, buf);
    }
}