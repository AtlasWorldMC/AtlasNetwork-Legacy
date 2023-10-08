package fr.atlasworld.network.networking;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;
import fr.atlasworld.network.networking.entities.NetworkClient;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.function.BiConsumer;

public class NetworkUtilities {
    public static final BiConsumer<SentPacket, Throwable> SENT_PACKET_RESOURCES_CLEANUP = ((sentPacket, throwable) -> sentPacket.cleanUpResources());
    public static final AttributeKey<NetworkClient> SESSION_ATTR_KEY = AttributeKey.valueOf("session");

    public static GenericFutureListener<ChannelFuture> cleanUpChannelSentPacket(PacketByteBuf buf) {
        return future -> {
            while (buf.isAccessible()) {
                buf.release();
            }
        };
    }

    public static final class StandardPackets {
        public static final String AUTHENTICATION_RESPONSE = "auth_response";
        public static final String REQUEST_FAILURE = "request_fail";
    }
}
