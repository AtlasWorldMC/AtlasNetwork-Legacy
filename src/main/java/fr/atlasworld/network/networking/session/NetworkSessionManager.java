package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.entities.INetworkSession;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Network implementation of the SessionManager
 * @see NetworkSessionManager
 */
public class NetworkSessionManager implements SessionManager {
    private final Map<UUID, INetworkSession> sessionHolder;

    public NetworkSessionManager(Map<UUID, INetworkSession> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public NetworkSessionManager() {
        this(new HashMap<>());
    }

    @Override
    public void addSession(INetworkSession session) {
        if (this.sessionHolder.containsKey(session.getId())) {
            throw new IllegalArgumentException("Sessions can only be saved once!");
        }

        this.sessionHolder.put(session.getId(), session);
    }

    @Override
    public void removeSession(INetworkSession session) {
        if (!this.sessionHolder.containsKey(session.getId())) {
            throw new IllegalArgumentException("Session '" + session.getId() + "' does not exist!");
        }

        this.sessionHolder.remove(session.getId());
    }

    @Override
    public Set<INetworkSession> getSessions() {
        return new HashSet<>(this.sessionHolder.values());
    }

    @Override
    public @Nullable INetworkSession getSession(UUID id) {
        return this.sessionHolder.values().stream()
                .filter(client -> client.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
