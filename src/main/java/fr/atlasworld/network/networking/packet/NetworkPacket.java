package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.session.ClientSession;

public interface NetworkPacket {
    String getKey();
    void onReceive(ClientSession client, PacketByteBuf packet) throws Exception;
}
