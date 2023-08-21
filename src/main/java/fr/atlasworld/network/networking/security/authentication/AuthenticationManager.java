package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.networking.auth.AuthenticationException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;

public interface AuthenticationManager {
    boolean isAuthenticated();
    void authenticate(Channel channel, PacketByteBuf buf) throws AuthenticationException;
}
