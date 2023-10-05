package fr.atlasworld.network.api.exception.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSource;

public class UnknownPacketException extends PacketException {
    public UnknownPacketException(String packetId, NetworkSource source) {
        super("Received an unknown packet '" + packetId + "' from '" + source + "!");
    }
}
