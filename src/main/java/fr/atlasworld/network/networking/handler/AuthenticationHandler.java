package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.exceptions.request.RequestUnauthenticatedException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.security.authentication.exceptions.AlreadyAuthenticatedException;
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
                throw new AlreadyAuthenticatedException("Connection already authenticated.");
            }

            UUID connectionId = this.authenticationManager.authenticate(ctx.channel(), buf);
            PacketByteBuf response = new PacketByteBuf(ctx.alloc().buffer())
                    .writeString("authentication")
                    .writeBoolean(true);

            ctx.channel().writeAndFlush(response);

            AtlasNetwork.logger.info("{} was successfully authenticated!", ctx.channel().remoteAddress());
            ctx.fireChannelActive();

            NetworkClient client = new NetworkClient(ctx.channel(), connectionId);

            this.sessionManager.addSession(client);
            client.attachData("conn_id", connectionId);
            client.onDisconnect(source -> this.sessionManager.removeSession(client));

            return;
        }

        if (!this.authenticationManager.isAuthenticated()) {
            throw new RequestUnauthenticatedException("Tried accessing '" + packet + "' while not authenticated.");
        }

        buf.clear();
        super.channelRead(ctx, buf);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        // Don't Trigger the Channel Active Event for other channel handlers yet
    }
}
