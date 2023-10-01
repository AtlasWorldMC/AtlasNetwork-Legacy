package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.entities.NetworkClient;

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
    void execute(String packet, NetworkClient client, PacketByteBuf buf);

    /**
     * Registers a packet to the manager
     * @param packet packet
     */
    void register(NetworkPacket packet);
}
