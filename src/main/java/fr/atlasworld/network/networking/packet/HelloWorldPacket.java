package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.entities.INetworkSource;
import fr.atlasworld.network.networking.entities.NetworkClient;
import org.jetbrains.annotations.TestOnly;

/**
 * Debugging packet, recommended to only use for debugging purposes, but it won't break anything if used in production.
 */
@TestOnly
public class HelloWorldPacket implements NetworkPacket {
    @Override
    public String getKey() {
        return "hello_world";
    }

    @Override
    public void onReceive(INetworkSource source, PacketByteBuf packet) {
        AtlasNetwork.logger.info("Received a Hello World packet from {}", source.remoteAddress());
        packet.release();
    }

    @Override
    public String toString() {
        return this.getKey();
    }
}
