package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.entities.NetworkClient;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Network implementation of the SessionManager
 * @see NetworkSessionManager
 */
public class NetworkSessionManager implements SessionManager {
    private final Map<UUID, NetworkClient> sessionHolder;

    public NetworkSessionManager(Map<UUID, NetworkClient> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public NetworkSessionManager() {
        this(new HashMap<>());
    }

    @Override
    public void addSession(NetworkClient session) {
        if (this.sessionHolder.containsKey(session.getId())) {
            throw new IllegalArgumentException("Sessions can only be saved once!");
        }

        this.sessionHolder.put(session.getId(), session);
    }

    @Override
    public void removeSession(Channel channel) {
        this.sessionHolder.remove(channel);
    }

    @Override
    public Set<NetworkClient> getSessions() {
        return new HashSet<>(this.sessionHolder.values());
    }

    @Override
    public @Nullable NetworkClient getSession(Channel channel) {
        return this.sessionHolder.get(channel);
    }

    @Override
    public @Nullable NetworkClient getSession(UUID id) {
        return this.sessionHolder.values().stream()
                .filter(client -> client.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
