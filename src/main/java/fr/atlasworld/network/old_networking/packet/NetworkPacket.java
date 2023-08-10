package fr.atlasworld.network.old_networking.packet;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.old_networking.entities.Client;

public interface NetworkPacket {
    String getIdentifier();
    void execute(Client client, PacketByteBuf data);
}
