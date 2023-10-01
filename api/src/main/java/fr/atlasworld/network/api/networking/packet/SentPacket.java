package fr.atlasworld.network.api.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSource;

/**
 * Represents a packet that has been sent.
 */
public interface SentPacket {

    /**
     * Cleans up request resources.
     * Only use this once your finished with the packet.
     * This will free-up resources but can have unexpected side effects like prematurely releasing a PacketByteBuf so be careful!
     */
    void cleanUpResources();

    /**
     * Returns a read only copy of the buffer that has been sent with the request.
     * @return sent buffer
     */
    PacketByteBuf getBufferData();

    /**
     * Returns the target which the packet has been sent to.
     * @return the target
     */
    NetworkSource getTarget();
}
