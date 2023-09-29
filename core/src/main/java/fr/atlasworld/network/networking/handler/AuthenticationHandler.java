package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.api.networking.PacketByteBuf;
import fr.atlasworld.network.exceptions.networking.auth.AuthenticationException;
import fr.atlasworld.network.networking.NetworkErrors;
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
                PacketByteBuf response = new PacketByteBufImpl(ctx.alloc().buffer())
                        .writeString("request_fail")
                        .writeString(NetworkErrors.ALREADY_AUTHED);

                ctx.channel().writeAndFlush(response);
                buf.release();
                return;
            }

            PacketByteBuf response;
            try {
                UUID connectionId = this.authenticationManager.authenticate(ctx.channel(), buf);
                response = new PacketByteBufImpl(ctx.alloc().buffer())
                        .writeString("auth_response")
                        .writeBoolean(true)
                        .writeString("SUCCESS");
                AtlasNetworkOld.logger.info("{} was successfully authenticated!", ctx.channel().remoteAddress());
                ctx.fireChannelActive();

                NetworkClient client = new NetworkClient(ctx.channel(), connectionId);
                this.sessionManager.addSession(ctx.channel(), client);
                ctx.channel().closeFuture().addListener(future -> this.sessionManager.removeSession(ctx.channel()));
            } catch (AuthenticationException e) {
                response = new PacketByteBufImpl(ctx.alloc().buffer())
                        .writeString("auth_response")
                        .writeBoolean(false)
                        .writeString(e.getNetworkFeedback());
                AtlasNetworkOld.logger.warn("{} authentication has failed: ", ctx.channel().remoteAddress(), e);
            }

            ctx.channel().writeAndFlush(response);
            buf.release();

            return;
        }

        if (!this.authenticationManager.isAuthenticated()) {
            AtlasNetworkOld.logger.error("{} tried accessing '{}' while not authenticated!", ctx.channel().remoteAddress(), packet);
            PacketByteBuf response = new PacketByteBufImpl(ctx.alloc().buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.NOT_AUTHED);

            ctx.channel().writeAndFlush(response);
            buf.release();
            return;
        }

        buf.readerIndex(0);
        super.channelRead(ctx, buf);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        // Don't Trigger the Channel Active Event for other channel handlers yet
    }
}
