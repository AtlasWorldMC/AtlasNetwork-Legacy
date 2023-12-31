package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.entities.NetworkSession;
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
    void addSession(NetworkSession session);

    /**
     * Unregisters a session, should only be used when the connection closes
     */
    void removeSession(NetworkSession session);

    /**
     * Gets all the active sessions
     * @return active sessions
     */
    Set<NetworkSession> getSessions();

    /**
     * Gets the session for specific id
     * @param id session id
     * @return Connection Placeholder
     */
    @Nullable NetworkSession getSession(UUID id);
}
