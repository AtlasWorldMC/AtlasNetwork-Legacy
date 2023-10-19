package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.exceptions.AuthenticationException;
import io.netty.channel.Channel;

import java.util.UUID;

/**
 * Handles the authentication of the socket
 */
public interface AuthenticationManager {
    /**
     * Checks if the connection is authenticated, value can only be changed privately
     * @return connection is authenticated
     */
    boolean isAuthenticated();

    /**
     * Authenticates the connection
     * @param channel the connection
     * @param buf packet data
     * @return Connection UUID
     * @throws AuthenticationException if the authentication fails
     */
    UUID authenticate(Channel channel, PacketByteBuf buf) throws AuthenticationException;
}
