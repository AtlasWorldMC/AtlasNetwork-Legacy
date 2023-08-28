package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.exceptions.networking.session.SessionAlreadyInUseException;
import fr.atlasworld.network.exceptions.networking.session.SessionException;
import fr.atlasworld.network.exceptions.networking.session.SessionNotUsedException;
import fr.atlasworld.network.networking.entities.NetworkClient;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NetworkSessionManager implements SessionManager {
    private final Map<Channel, NetworkClient> channelSessionHolder;
    private final Map<UUID, NetworkClient> idSessionHolder;

    public NetworkSessionManager(Map<Channel, NetworkClient> channelSessionHolder, Map<UUID, NetworkClient> idSessionHolder) {
        this.channelSessionHolder = channelSessionHolder;
        this.idSessionHolder = idSessionHolder;
    }

    public NetworkSessionManager() {
        this(new HashMap<>(), new HashMap<>());
    }

    @Override
    public void addSession(Channel channel, NetworkClient session) throws SessionException {
        if (this.channelSessionHolder.containsKey(channel)) {
            throw new SessionAlreadyInUseException(String.valueOf(channel.remoteAddress()));
        }
        this.channelSessionHolder.put(channel, session);
        UUID connectionId = session.getUuid();

        if (this.idSessionHolder.containsKey(connectionId)) {
            throw new SessionAlreadyInUseException(String.valueOf(channel.remoteAddress()));
        }
        this.idSessionHolder.put(connectionId, session);
    }

    @Override
    public void removeSession(Channel channel) throws SessionException {
        if (!this.channelSessionHolder.containsKey(channel)) {
            throw new SessionNotUsedException(String.valueOf(channel.remoteAddress()));
        }
        this.channelSessionHolder.remove(channel);
    }

    @Override
    public Set<NetworkClient> getSessions() {
        return new HashSet<>(this.channelSessionHolder.values());
    }

    @Override
    public @Nullable NetworkClient getSession(Channel channel) {
        return this.channelSessionHolder.get(channel);
    }

    @Override
    public @Nullable NetworkClient getSession(UUID uuid) {
        return this.idSessionHolder.get(uuid);
    }
}
