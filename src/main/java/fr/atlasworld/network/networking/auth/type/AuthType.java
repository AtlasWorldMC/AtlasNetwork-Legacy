package fr.atlasworld.network.networking.auth.type;

import fr.atlasworld.network.networking.PacketByteBuf;

public interface AuthType {
    AuthTypeResults authenticate(PacketByteBuf data);

    record AuthTypeResults(boolean successful, String message, String userId) {
    }
}
