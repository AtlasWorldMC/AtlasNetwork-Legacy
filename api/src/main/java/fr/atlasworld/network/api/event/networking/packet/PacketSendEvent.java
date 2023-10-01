package fr.atlasworld.network.api.event.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;

/**
 * Triggered when a packet has been sent.
 */
public class PacketSendEvent extends PacketNetworkEvent {
    public PacketSendEvent(NetworkSocket socket, NetworkSource source, PacketByteBuf dataBuffer) {
        super(socket, source, dataBuffer);
    }
}
