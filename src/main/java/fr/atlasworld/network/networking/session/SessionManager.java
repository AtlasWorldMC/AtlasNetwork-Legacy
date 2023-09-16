package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.exceptions.networking.session.SessionException;
import fr.atlasworld.network.networking.entities.NetworkClient;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * Session Manager, handles the sessions of the connections
 */
public interface SessionManager {
    /**
     * Registers a session
     * @param channel Connection IO Channel
     * @param session Connection Placeholder
     */
    void addSession(Channel channel, NetworkClient session) throws SessionException;

    /**
     * Unregisters a session, should only be used when the connection closes
     * @param channel Connection IO Channel
     */
    void removeSession(Channel channel) throws SessionException;

    /**
     * Gets all the active sessions
     * @return active sessions
     */
    Set<NetworkClient> getSessions();

    /**
     * Gets the session for a specific channel
     * @param channel Connection IO Channel
     * @return Connection Placeholder
     */
    @Nullable NetworkClient getSession(Channel channel);
}
