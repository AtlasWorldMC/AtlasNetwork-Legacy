package fr.atlasworld.network.api.event.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;

/**
 * Triggered when a packet has been received.
 */
public class PacketReceivedEvent extends PacketNetworkEvent {
    public PacketReceivedEvent(NetworkSocket socket, NetworkSource source, PacketByteBuf dataBuffer) {
        super(socket, source, dataBuffer);
    }
}
