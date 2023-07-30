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

            PacketByteBuf helloWorldBuf = new PacketByteBuf(Unpooled.buffer())
                    .writeString("hello_world");

            ctx.channel().writeAndFlush(helloWorldBuf.getParent());
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(new PacketByteBuf(Unpooled.buffer())
                .writeString("authenticate")
                .writeString("c9987370-92f5-4d38-a9bf-4293183f7546")
                .writeString("xModeBb4yZ0gdXlHMD-A5rkmDed4M5PI")
                .getParent());
    }
}
