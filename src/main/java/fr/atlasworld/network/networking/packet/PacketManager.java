package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.entities.NetworkClient;

public interface PacketManager {
    void execute(String packet, NetworkClient client, PacketByteBuf buf);
    void register(NetworkPacket packet);
}
