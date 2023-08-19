package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.security.authentication.AuthenticationResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationHandler(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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

            AuthenticationResult result = this.authenticationManager.authenticate(ctx.channel(), buf);

            if (result.success()) {
                AtlasNetwork.logger.info("{} was successfully authenticated!", ctx.channel().remoteAddress());
            } else {
                AtlasNetwork.logger.warn("{} authentication has failed: {}", ctx.channel().remoteAddress(), result.message());
            }

            PacketByteBuf response = PacketByteBuf.create()
                    .writeString("auth_response")
                    .writeBoolean(result.success())
                    .writeString(result.message());

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
}
