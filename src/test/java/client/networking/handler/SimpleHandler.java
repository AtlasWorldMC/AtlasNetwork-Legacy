package client.networking.handler;

import client.networking.ClientEncryptionManager;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import client.NetworkClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SimpleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;

        String packet = buf.readString();

        if (packet.equals("request_fail")) {
            NetworkClient.logger.error("Request failed: {}", buf.readString());
            return;
        }

        if (packet.equals("encryption_handshake")) {
            NetworkClient.logger.info("Handshake response successful: " + buf.readBoolean());

            PacketByteBuf authRequest = PacketByteBuf.create()
                    .writeString("auth")
                    .writeUuid(UUID.fromString("753e6b22-90fe-45d9-b6bc-988d6c6a5f48")) // writeString() should work as well
                    .writeString("CmcZFs40ivhm9plUJnZtDuCmP8NzAEfo");

            ctx.channel().writeAndFlush(authRequest);
            return;
        }

        if (packet.equals("auth_response")) {
            boolean successful = buf.readBoolean();
            String message = buf.readString();

            if (successful) {
                AtlasNetwork.logger.info("Successfully authed!");
            } else {
                AtlasNetwork.logger.error("Authentication failed: {}", message);
            }
            return;
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {

    }
}
