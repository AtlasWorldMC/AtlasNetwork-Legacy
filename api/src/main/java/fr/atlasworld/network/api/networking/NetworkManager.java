package fr.atlasworld.network.api.networking;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface NetworkManager {
    /**
     * Retrieve all active connections
     */
    Set<NetworkSource> getConnectedClients();

    /**
     * Retrieve a connected client with it's uuid
     */
    @Nullable NetworkSource getClient(UUID uuid);

    /**
     * Send a packet to all active connections
     * @param buf packet
     * @return future of the sent packet request
     */
    Stream<CompletableFuture<SentPacket>> sendPacketToAllClients(PacketByteBuf buf);
}
