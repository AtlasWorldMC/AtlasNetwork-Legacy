package fr.atlasworld.network.tests.networking.handler;

import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.tests.NetworkClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class SimpleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        NetworkClient.logger.info("Packet received!");

        PacketByteBuf buf = new PacketByteBuf((ByteBuf) msg);

        String packet = buf.readString();

        if (packet.equals("request_fail")) {
            NetworkClient.logger.info("Request failed: {}", buf.readString());
            return;
        }

        if (packet.equals("authenticate_response")) {
            boolean successful = buf.readBoolean();

            if (successful) {
                NetworkClient.logger.info("Successfully authed: " + buf.readString());
            } else {
                NetworkClient.logger.info("Authentification failed: " + buf.readString());
            }
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(new PacketByteBuf(Unpooled.buffer())
                .writeString("authenticate")
                .writeString("827e799d-e3e3-464a-a599-cbacc9519ec0").writeString("dZbh87C_2FkWtYEZfthADrrLWNRV5e8k")
                .getParent());
        NetworkClient.logger.info("Sent packet!");
    }
}
