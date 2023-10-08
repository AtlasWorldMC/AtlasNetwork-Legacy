package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.api.exception.networking.session.SessionException;
import fr.atlasworld.network.api.networking.NetworkManager;
import fr.atlasworld.network.networking.entities.NetworkClient;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * Session Manager, handles the sessions of the connections
 */
public interface SessionManager extends NetworkManager {
    /**
     * Registers a session
     * @param session Connection Placeholder
     */
    void addSession(NetworkClient session) throws SessionException;

    /**
     * Unregisters a session, should only be used when the connection closes
     */
    void removeSession(NetworkClient session) throws SessionException;

    /**
     * Unregisters a session, should only be used when the connection closes
     */
    void removeSession(UUID uuid) throws SessionException;
}
