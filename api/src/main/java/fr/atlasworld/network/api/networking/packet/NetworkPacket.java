package fr.atlasworld.network.api.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSource;

public interface NetworkPacket {
    void getId();
    void onReceived(PacketByteBuf buf, NetworkSource source) throws Exception;
}
