package fr.atlasworld.network.networking.packet.exceptions;

import fr.atlasworld.network.networking.exceptions.NetworkException;

public class PacketException extends NetworkException {
    public PacketException() {
    }

    public PacketException(String message) {
        super(message);
    }

    public PacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketException(Throwable cause) {
        super(cause);
    }
}
