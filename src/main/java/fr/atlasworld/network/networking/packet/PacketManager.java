package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.session.ClientSession;

public interface PacketManager {
    void execute(String packet, ClientSession client, PacketByteBuf buf);
    void register(NetworkPacket packet);
}
