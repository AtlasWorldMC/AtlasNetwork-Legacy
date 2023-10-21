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
        if (this.sessionHolder.containsKey(session.id())) {
            throw new IllegalArgumentException("Sessions can only be saved once!");
        }

        this.sessionHolder.put(session.id(), session);
    }

    @Override
    public void removeSession(INetworkSession session) {
        if (!this.sessionHolder.containsKey(session.id())) {
            throw new IllegalArgumentException("Session '" + session.id() + "' does not exist!");
        }

        this.sessionHolder.remove(session.id());
    }

    @Override
    public Set<INetworkSession> getSessions() {
        return new HashSet<>(this.sessionHolder.values());
    }

    @Override
    public @Nullable INetworkSession getSession(UUID id) {
        return this.sessionHolder.values().stream()
                .filter(client -> client.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
