package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.entities.Client;

public interface NetworkPacket {
    String getIdentifier();
    void execute(Client client, PacketByteBuf data);
}
