package fr.atlasworld.network.networking.packet.exceptions;

public class PacketSendingException extends PacketException {
    public PacketSendingException(String message) {
        super(message);
    }

    public PacketSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketSendingException(Throwable cause) {
        super(cause);
    }
}
