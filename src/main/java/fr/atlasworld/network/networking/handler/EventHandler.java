package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.auth.AuthManager;
import fr.atlasworld.network.networking.client.SessionManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventHandler extends ChannelInboundHandlerAdapter {
    private final AuthManager authManager;
    private final SessionManager sessionManager;

    public EventHandler(AuthManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    //Exception Thrown
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Common ByteBuf parsing error
        if (cause instanceof IndexOutOfBoundsException exception) {
            AtlasNetwork.logger.warn("Unable to handle {} packet, Packet formatting or version invalid!", ctx.channel().remoteAddress());

            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.INCORRECT_PACKET_FORMAT);

            ctx.channel().writeAndFlush(response.getParent()).addListener(future -> response.release());

            return;
        }


    }

    //Connection opened
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {

    }

    //Connection closed
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.authManager.invalidateToken(ctx.channel());
        this.sessionManager.deleteSession(ctx.channel());
        AtlasNetwork.logger.info("{} disconnected!", ctx);
    }
}
