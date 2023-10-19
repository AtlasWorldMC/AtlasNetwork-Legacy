package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.exceptions.PacketSendingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {
    @Override
    @SuppressWarnings("deprecation")
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof PacketSendingException e) {
            AtlasNetwork.logger.error("[FATAL] THIS SHOULD NOT HAPPEN IN PRODUCTION. YOU TRIED TO SEND ANOTHER OBJECT TYPE THROUGH THE SOCKET", e);
        }
    }
}
