package fr.atlasworld.network.api.exception.networking.packet;

public class PacketExecutionException extends PacketException {
    public PacketExecutionException(String packetId, Throwable cause) {
       super("Something failed while trying to execute '" + packetId + "':", cause);
    }
}
