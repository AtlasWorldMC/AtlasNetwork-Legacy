package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.exception.SessionAlreadyRegisteredException;
import fr.atlasworld.network.networking.exception.SessionNotRegisteredException;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NetworkSessionManager implements SessionManager {
    private final Map<Channel, ClientSession> sessionHolder;

    public NetworkSessionManager(Map<Channel, ClientSession> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    @Override
    public void addSession(Channel channel, ClientSession session) {
        if (this.sessionHolder.containsKey(channel)) {
            throw new SessionAlreadyRegisteredException(String.valueOf(channel.remoteAddress()));
        }
        this.sessionHolder.put(channel, session);
    }

    @Override
    public void removeSession(Channel channel) {
        if (!this.sessionHolder.containsKey(channel)) {
            throw new SessionNotRegisteredException(String.valueOf(channel.remoteAddress()));
        }
        this.sessionHolder.remove(channel);
    }

    @Override
    public Set<ClientSession> getSessions() {
        return new HashSet<>(this.sessionHolder.values());
    }

    @Override
    public @Nullable ClientSession getSession(Channel channel) {
        return this.sessionHolder.get(channel);
    }
}
