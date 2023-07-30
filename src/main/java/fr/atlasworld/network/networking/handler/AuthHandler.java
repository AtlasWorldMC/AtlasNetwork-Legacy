package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.auth.AuthResult;
import fr.atlasworld.network.networking.auth.AuthentificationManager;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private final AuthentificationManager authManager;
    private final SessionManager sessionManager;

    public AuthHandler(AuthentificationManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = new PacketByteBuf((ByteBuf) msg);

        String token = buf.readString();

        //Check for an authenticate packet
        if (token.equals("authenticate")) {
            if (this.sessionManager.hasSession(ctx.channel())) {
                this.sessionManager.deleteSession(ctx.channel());
                AtlasNetwork.logger.info("{} sent a authentification packet when already authenticated.", ctx.channel().remoteAddress());
            }

            AuthResult result = this.authManager.authenticate(ctx.channel(), buf);

            if (result.success()) {
                this.sessionManager.createSession(ctx.channel());
                AtlasNetwork.logger.info("Authentification successful for {}", ctx.channel().remoteAddress());
            } else {
                AtlasNetwork.logger.error("Authentification failed for {}", ctx.channel().remoteAddress());
            }

            //Build response
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("authenticate_response")
                    .writeByteBuf(result.toByteBuf(Unpooled.buffer()));

            //Send response, and free memory once sent
            ctx.channel().writeAndFlush(response.getParent());

            //Clear memory
            buf.release();
            response.release();

            return;
        }


        if (!this.sessionManager.hasSession(ctx.channel())) {
            AtlasNetwork.logger.error("{} sent a packet without being authenticated!", ctx.channel().remoteAddress());

            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.NOT_AUTHED);

            ctx.channel().writeAndFlush(response.getParent());

            //Clear memory
            buf.release();
            response.release();

            return;
        }

        buf.readerIndex(0);

        //Pass the data to the other handlers in the pipeline
        super.channelRead(ctx, buf);
    }
}
