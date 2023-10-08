package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.exception.networking.requests.RequestFailException;
import fr.atlasworld.network.api.exception.networking.authentication.AlreadyAuthenticatedException;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.NetworkUtilities;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles authentication of the socket and assigns sessions to incoming connections
 */
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
    private final AuthenticationManager authenticationManager;
    private final SessionManager sessionManager;

    public AuthenticationHandler(AuthenticationManager authenticationManager, SessionManager sessionManager) {
        this.authenticationManager = authenticationManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;

        String packet = buf.readString();
        if (packet.equals("auth")) {
            if (this.authenticationManager.isAuthenticated()) {
                throw new AlreadyAuthenticatedException();
            }

            UUID connectionId = this.authenticationManager.authenticate(ctx.channel(), buf);
            PacketByteBuf response = new PacketByteBufImpl(ctx.alloc().buffer())
                    .writeString("auth_response")
                    .writeBoolean(true)
                    .writeString("SUCCESS");

            AtlasNetwork.logger.info("{} was successfully authenticated!", ctx.channel().remoteAddress());

            NetworkClient client = new NetworkClient(connectionId, ctx.channel());
            this.sessionManager.addSession(client);

            ctx.channel().attr(NetworkUtilities.SESSION_ATTR_KEY).set(client);
            ctx.channel().closeFuture().addListener(future -> this.sessionManager.removeSession(connectionId));
            ctx.channel().writeAndFlush(response).addListener(NetworkUtilities.cleanUpChannelSentPacket(buf));

            ctx.fireChannelActive();
            return;
        }

        if (!this.authenticationManager.isAuthenticated()) {
            throw new RequestFailException(ctx.channel().remoteAddress() + " tried accessing '" + packet + "' while not authenticated!", "NOT_AUTHED");
        }

        buf.clear();
        super.channelRead(ctx, buf);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        // Don't Trigger the Channel Active Event for other channel handlers yet
    }
}
