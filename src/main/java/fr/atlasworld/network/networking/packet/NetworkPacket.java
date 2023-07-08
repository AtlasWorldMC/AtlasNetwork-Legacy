package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.Client;
import fr.atlasworld.network.networking.PacketByteBuf;

public interface NetworkPacket {
    String getIdentifier();
    void execute(Client client, PacketByteBuf data);
}
