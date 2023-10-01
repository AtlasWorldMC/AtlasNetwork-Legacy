package fr.atlasworld.network.api.networking;

import fr.atlasworld.network.api.NetworkEntity;
import fr.atlasworld.network.api.concurrent.Future;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;

import java.net.InetSocketAddress;

/**
 * Network source is a wrapper for a client connected on the socket
 */
public interface NetworkSource extends NetworkEntity {
    /**
     * Send a packet to the source
     * @param buf packet data
     * @return the sent packet future
     */
    Future<SentPacket> sendPacket(PacketByteBuf buf);

    /**
     * Retrieve the remote address
     */
    InetSocketAddress remoteAddress();

    /**
     * Disconnects a client.
     * @param reason reason for disconnecting the client.
     * @return true if the client has been successfully disconnected;
     */
    boolean disconnect(String reason);
}
