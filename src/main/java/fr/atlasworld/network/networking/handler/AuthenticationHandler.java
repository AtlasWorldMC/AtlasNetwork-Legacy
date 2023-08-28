package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.exceptions.networking.auth.AuthenticationException;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
                PacketByteBuf response = PacketByteBuf.create()
                        .writeString("request_fail")
                        .writeString(NetworkErrors.ALREADY_AUTHED);

                ctx.channel().writeAndFlush(response);
                buf.release();
                return;
            }

            PacketByteBuf response;
            try {
                UUID connectionId = this.authenticationManager.authenticate(ctx.channel(), buf);
                response = PacketByteBuf.create()
                        .writeString("auth_response")
                        .writeBoolean(true)
                        .writeString("SUCCESS");
                AtlasNetwork.logger.info("{} was successfully authenticated!", ctx.channel().remoteAddress());
                ctx.fireChannelActive();

                NetworkClient client = new NetworkClient(ctx.channel(), connectionId);
                this.sessionManager.addSession(ctx.channel(), client);
                ctx.channel().closeFuture().addListener(future -> this.sessionManager.removeSession(ctx.channel()));
            } catch (AuthenticationException e) {
                response = PacketByteBuf.create()
                        .writeString("auth_response")
                        .writeBoolean(false)
                        .writeString(e.getNetworkFeedback());
                AtlasNetwork.logger.warn("{} authentication has failed: ", ctx.channel().remoteAddress(), e);
            }
            ctx.channel().writeAndFlush(response);
            buf.release();

            return;
        }

        if (!this.authenticationManager.isAuthenticated()) {
            AtlasNetwork.logger.error("{} tried accessing '{}' while not authenticated!", ctx.channel().remoteAddress(), packet);
            PacketByteBuf response = PacketByteBuf.create()
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
