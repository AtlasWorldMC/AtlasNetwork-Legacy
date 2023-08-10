package client.networking.handler;

import client.networking.ClientEncryptionManager;
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

public class SimpleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = (PacketByteBuf) msg;

        String packet = buf.readString();

        if (packet.equals("request_fail")) {
            NetworkClient.logger.info("Request failed: {}: {}", buf.readString(), buf.readString());
            return;
        }

        if (packet.equals("encryption_handshake")) {
            NetworkClient.logger.info("Handshake response successful: " + buf.readBoolean());
            return;
        }

        System.out.println("Unknown: " + packet);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {

    }
}
