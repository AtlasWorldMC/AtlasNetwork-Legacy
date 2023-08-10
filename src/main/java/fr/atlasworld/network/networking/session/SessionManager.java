package fr.atlasworld.network.networking.session;

import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface SessionManager {
    /**
     * Registers a session
     * @param channel Connection IO Channel
     * @param session Connection Placeholder
     */
    void addSession(Channel channel, ClientSession session);

    /**
     * Unregisters a session, should only be used when the connection closes
     * @param channel Connection IO Channel
     */
    void removeSession(Channel channel);

    /**
     * Gets all the active sessions
     * @return active sessions
     */
    Set<ClientSession> getSessions();

    /**
     * Gets the session for a specific channel
     * @param channel Connection IO Channel
     * @return Connection Placeholder
     */
    @Nullable ClientSession getSession(Channel channel);
}
