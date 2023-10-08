package fr.atlasworld.network.api.exception.networking.packet;

public class InvalidPacketException extends PacketException {
    public InvalidPacketException() {
        super("Invalid packet version or format!", "INCORRECT_PACKET_FORMAT");
    }

    public InvalidPacketException(String msg) {
        super(msg, "INCORRECT_PACKET_FORMAT");
    }
}
