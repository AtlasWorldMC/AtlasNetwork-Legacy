package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.entities.NetworkClient;

public class HelloWorldPacket implements NetworkPacket {
    @Override
    public String getKey() {
        return "hello_world";
    }

    @Override
    public void onReceive(NetworkClient client, PacketByteBuf packet) {
        AtlasNetwork.logger.info("Received a Hello World packet from {}", client.remoteAddress());
        packet.release();
    }

    @Override
    public String toString() {
        return this.getKey();
    }
}
