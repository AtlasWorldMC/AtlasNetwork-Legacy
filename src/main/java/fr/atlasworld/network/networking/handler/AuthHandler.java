package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.auth.AuthManager;
import fr.atlasworld.network.networking.auth.AuthResponses;
import fr.atlasworld.network.networking.auth.AuthResult;
import fr.atlasworld.network.networking.client.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private final AuthManager authManager;
    private final SessionManager sessionManager;

    public AuthHandler(AuthManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = new PacketByteBuf((ByteBuf) msg);

        String token = buf.readString();

        //Check for an authenticate packet
        if (token.equals("authenticate")) {
            int authMethodId = buf.readInt();
            AuthResult result = this.authManager.authenticate(ctx.channel(), authMethodId, buf);

            if (result.successful()) {
                this.sessionManager.createSession(ctx.channel(), result.userId());
            }

            //Build response
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("authenticate_response")
                    .writeBoolean(result.successful())
                    .writeString(result.message())
                    .writeString(result.token());

            //Send response, and free memory once sent
            ctx.channel().writeAndFlush(response.getParent()).addListener(future -> {
                buf.release();
                response.release();
            });

            return;
        }

        //Validate token
        if (!this.authManager.validate(ctx.channel(), token)) {
            AtlasNetwork.logger.error("{} sent a packet with a invalid session token!", ctx.channel().remoteAddress());

            //Build response
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("authentication_error")
                    .writeString(AuthResponses.INVALID_OR_MISSING_TOKEN);

            //Send response, and free memory once sent
            ctx.channel().writeAndFlush(response.getParent()).addListener(future -> {
                buf.release();
                response.release();
            });

            return;
        }

        //Pass the data to the other handlers in the pipeline
        super.channelRead(ctx, msg);
    }
}
