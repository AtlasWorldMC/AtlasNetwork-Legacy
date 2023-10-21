package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.entities.INetworkSession;
import fr.atlasworld.network.networking.entities.INetworkSource;
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
     */
    void addSession(INetworkSession session);

    /**
     * Unregisters a session, should only be used when the connection closes
     */
    void removeSession(INetworkSession session);

    /**
     * Gets all the active sessions
     * @return active sessions
     */
    Set<INetworkSession> getSessions();

    /**
     * Gets the session for specific id
     * @param id session id
     * @return Connection Placeholder
     */
    @Nullable INetworkSession getSession(UUID id);
}
