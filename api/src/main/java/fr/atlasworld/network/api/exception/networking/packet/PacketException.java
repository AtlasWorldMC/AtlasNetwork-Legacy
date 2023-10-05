package fr.atlasworld.network.api.exception.networking.packet;

import fr.atlasworld.network.api.exception.networking.NetworkException;

public class PacketException extends NetworkException {
    public PacketException() {
        super();
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
