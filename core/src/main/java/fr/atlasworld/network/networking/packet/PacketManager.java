package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.exception.networking.packet.PacketException;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.PacketHandler;
import fr.atlasworld.network.networking.entities.NetworkClient;

/**
 * Packet manager, executes and registers packet for the socket
 */
public interface PacketManager extends PacketHandler {
    /**
     * Executes a packet
     * @param packet packet id
     * @param client client that sent the packet
     * @param buf packet data
     */
    void execute(String packet, NetworkClient client, PacketByteBuf buf) throws PacketException;
}
