package fr.atlasworld.network.networking.packet.exceptions;

public class PacketParsingException extends PacketException {
    public PacketParsingException(String message) {
        super(message);
    }

    public PacketParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketParsingException(Throwable cause) {
        super(cause);
    }
}
