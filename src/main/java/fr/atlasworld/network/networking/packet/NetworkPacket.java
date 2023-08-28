package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.entities.NetworkClient;

public interface NetworkPacket {
    String getKey();
    void onReceive(NetworkClient client, PacketByteBuf packet);
}
