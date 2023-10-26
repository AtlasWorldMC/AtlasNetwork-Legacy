package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.entities.NetworkSource;

/**
 * Packet manager, executes and registers packet for the socket
 */
public interface PacketManager {
    /**
     * Executes a packet
     * @param packet packet id
     * @param client client that sent the packet
     * @param buf packet data
     */
    void execute(String packet, NetworkSource client, PacketByteBuf buf);

    /**
     * Registers a packet to the manager
     * @param packet packet
     */
    void register(NetworkPacket packet);
}
