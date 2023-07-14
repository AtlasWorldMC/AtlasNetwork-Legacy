package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.PacketByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventHandler extends ChannelInboundHandlerAdapter {

    //Exception Thrown
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Common ByteBuf parsing error
        if (cause instanceof IndexOutOfBoundsException exception) {
            AtlasNetwork.logger.warn("Unable to handle {} packet's, Packet formatting or version invalid!", ctx.channel().remoteAddress());

            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.INCORRECT_PACKET_FORMAT);

            ctx.channel().writeAndFlush(response.getParent());

            return;
        }
    }

    //Connection opened
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        AtlasNetwork.logger.info("{} connected!", ctx.channel().remoteAddress());
    }

    //Connection closed
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        AtlasNetwork.logger.info("{} disconnected!", ctx.channel().remoteAddress());
    }
}
