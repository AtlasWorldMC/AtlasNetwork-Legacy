package fr.atlasworld.network.old_networking.session;

import fr.atlasworld.network.exceptions.SessionException;
import fr.atlasworld.network.old_networking.entities.Client;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SessionManager {
    private final Map<Channel, Client> sessionHolder;

    public SessionManager(Map<Channel, Client> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public void createSession(Channel channel) {
        if (this.sessionHolder.containsKey(channel)) {
            throw new SessionException("Cannot create multiple sessions for " + channel.remoteAddress());
        }

        //Delete session if the channel closes
        channel.closeFuture().addListener(future -> this.deleteSession(channel));

        Client client = new Client(channel);
        this.sessionHolder.put(channel, client);
    }

    public void deleteSession(Channel channel) {
        this.sessionHolder.remove(channel);
    }

    public boolean hasSession(Channel channel) {
        return this.sessionHolder.containsKey(channel);
    }

    public Client getSession(Channel channel) {
        return this.sessionHolder.get(channel);
    }

    public Stream<Client> getAllSessions() {
        return this.sessionHolder.values().stream();
    }

    //Static fields
    public static SessionManager manager;

    public static SessionManager getManager() {
        if(manager == null) {
            manager = new SessionManager(new HashMap<>());
        }

        return manager;
    }
}
