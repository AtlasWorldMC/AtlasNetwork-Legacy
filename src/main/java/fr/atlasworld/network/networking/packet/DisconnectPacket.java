package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.entities.NetworkSource;
import io.netty.buffer.ByteBufAllocator;

public class DisconnectPacket implements NetworkPacket {
    public static final String KEY = "disconnect";
    public static final String DISCONNECT_REASON_VALUE_KEY = "disc_reason";

    @Override
    public String getKey() {
        return DisconnectPacket.KEY;
    }

    @Override
    public void onReceive(NetworkSource source, PacketByteBuf packet) throws Throwable {
        String reason = packet.readString();

        source.attachData(DisconnectPacket.DISCONNECT_REASON_VALUE_KEY, reason);
    }

    public static PacketByteBuf createPacket(String reason, ByteBufAllocator alloc) {
        return new PacketByteBuf(alloc.buffer())
                .writeString(DisconnectPacket.KEY)
                .writeString(reason);
    }
}
