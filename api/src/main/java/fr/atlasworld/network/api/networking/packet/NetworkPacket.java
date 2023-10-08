package fr.atlasworld.network.api.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSource;

public interface NetworkPacket {
    String getId();
    void onReceived(PacketByteBuf buf, NetworkSource source) throws Exception;
}
