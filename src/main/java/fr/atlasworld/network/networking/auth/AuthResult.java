package fr.atlasworld.network.networking.auth;

import fr.atlasworld.network.networking.PacketByteBuf;
import io.netty.buffer.ByteBuf;

public record AuthResult(boolean success, String message) {
    public PacketByteBuf toByteBuf(ByteBuf parent) {
        return new PacketByteBuf(parent).writeBoolean(success).writeString(message);
    }
}
