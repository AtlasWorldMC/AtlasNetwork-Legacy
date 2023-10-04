package fr.atlasworld.network.api.networking;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface NetworkManager {
    /**
     * Retrieve all active connections
     */
    Set<NetworkSource> getConnectedClients();

    /**
     * Send a packet to all active connections
     * @param buf packet
     * @return future of the sent packet request
     */
    CompletableFuture<SentPacket> sendPacketToAllClients(PacketByteBuf buf);
}
