package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.entities.NetworkClient;

/**
 * Network Packet, implement this interface and register it to create a new packet
 */
public interface NetworkPacket {
    /**
     * Packet key/identifier
     * @return packet key
     */
    String getKey();

    /**
     * Called when the packet is received on the socket
     * @param client remote that sent the packet
     * @param packet packet data
     */
    void onReceive(NetworkClient client, PacketByteBuf packet);
}
