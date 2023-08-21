package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.exceptions.networking.session.SessionAlreadyInUseException;
import fr.atlasworld.network.exceptions.networking.session.SessionException;
import fr.atlasworld.network.exceptions.networking.session.SessionNotUsedException;
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
    public void addSession(Channel channel, ClientSession session) throws SessionException {
        if (this.sessionHolder.containsKey(channel)) {
            throw new SessionAlreadyInUseException(String.valueOf(channel.remoteAddress()));
        }
        this.sessionHolder.put(channel, session);
    }

    @Override
    public void removeSession(Channel channel) throws SessionException {
        if (!this.sessionHolder.containsKey(channel)) {
            throw new SessionNotUsedException(String.valueOf(channel.remoteAddress()));
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
