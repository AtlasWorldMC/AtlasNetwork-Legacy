package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.entities.NetworkSession;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Network implementation of the SessionManager
 * @see NetworkSessionManager
 */
public class NetworkSessionManager implements SessionManager {
    private final Map<UUID, NetworkSession> sessionHolder;

    public NetworkSessionManager(Map<UUID, NetworkSession> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public NetworkSessionManager() {
        this(new HashMap<>());
    }

    @Override
    public void addSession(NetworkSession session) {
        if (this.sessionHolder.containsKey(session.id())) {
            throw new IllegalArgumentException("Sessions can only be saved once!");
        }

        this.sessionHolder.put(session.id(), session);
    }

    @Override
    public void removeSession(NetworkSession session) {
        if (!this.sessionHolder.containsKey(session.id())) {
            throw new IllegalArgumentException("Session '" + session.id() + "' does not exist!");
        }

        this.sessionHolder.remove(session.id());
    }

    @Override
    public Set<NetworkSession> getSessions() {
        return new HashSet<>(this.sessionHolder.values());
    }

    @Override
    public @Nullable NetworkSession getSession(UUID id) {
        return this.sessionHolder.values().stream()
                .filter(client -> client.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
