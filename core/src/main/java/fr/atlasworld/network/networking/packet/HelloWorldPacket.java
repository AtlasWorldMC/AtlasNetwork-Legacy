package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.AtlasNetworkOld;
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
    public void onReceive(NetworkClient client, PacketByteBuf packet) {
        AtlasNetworkOld.logger.info("Received a Hello World packet from {}", client.remoteAddress());
        packet.release();
    }

    @Override
    public String toString() {
        return this.getKey();
    }
}
