package fr.atlasworld.network.api.networking.packet;

import java.util.Set;

/**
 * Handles the packet, registers, executes ect.
 * If you want to add custom packets for your module do it here.
 * Don't forget to update your clients! Else your packets won't ever be received.
 */
public interface PacketHandler {
    /**
     * Register a packet
     * @param packet packet
     */
    void registerPacket(NetworkPacket packet);

    /**
     * Unregisters a packet
     * @param id packet's id
     */
    void unregisterPacket(String id);

    /**
     * Unregisters a packet
     * @param packet packet
     */
    void unregisterPacket(NetworkPacket packet);

    /**
     * Retrieve all registered packets
     */
    Set<NetworkPacket> getPackets();
}
