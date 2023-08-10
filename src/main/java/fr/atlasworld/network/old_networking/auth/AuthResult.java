package fr.atlasworld.network.old_networking.auth;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;

public record AuthResult(boolean success, String message) {
    public PacketByteBuf toByteBuf(ByteBuf parent) {
        return new PacketByteBuf(parent).writeBoolean(success).writeString(message);
    }
}
