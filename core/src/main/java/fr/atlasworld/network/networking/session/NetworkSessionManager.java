package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.api.exception.networking.session.SessionAlreadyInUseException;
import fr.atlasworld.network.api.exception.networking.session.SessionException;
import fr.atlasworld.network.api.exception.networking.session.SessionNotUsedException;
import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;
import fr.atlasworld.network.networking.entities.NetworkClient;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Network implementation of the SessionManager
 * @see NetworkSessionManager
 */
public class NetworkSessionManager implements SessionManager {
    private final Map<UUID, NetworkClient> channelSessionHolder;

    public NetworkSessionManager(Map<UUID, NetworkClient> channelSessionHolder) {
        this.channelSessionHolder = channelSessionHolder;
    }

    public NetworkSessionManager() {
        this(new HashMap<>());
    }

    @Override
    public void addSession(NetworkClient session) throws SessionException {
        if (this.channelSessionHolder.containsKey(session.getId())) {
            throw new SessionAlreadyInUseException(String.valueOf(session.remoteAddress()));
        }
        this.channelSessionHolder.put(session.getId(), session);
    }

    @Override
    public void removeSession(NetworkClient session) throws SessionException {
        this.removeSession(session.getId());
    }

    @Override
    public void removeSession(UUID uuid) throws SessionException {
        if (!this.channelSessionHolder.containsKey(uuid)) {
            throw new SessionNotUsedException(String.valueOf(uuid));
        }
        this.channelSessionHolder.remove(uuid);
    }

    @Override
    public Set<NetworkSource> getConnectedClients() {
        return new HashSet<>(this.channelSessionHolder.values());
    }

    @Override
    public @Nullable NetworkSource getClient(UUID uuid) {
        return this.channelSessionHolder.get(uuid);
    }

    @Override
    public Stream<CompletableFuture<SentPacket>> sendPacketToAllClients(PacketByteBuf buf) {
        return this.channelSessionHolder.values()
                .stream()
                .map(client -> client.sendPacket(buf));
    }
}
