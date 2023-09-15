package fr.atlasworld.network.exceptions.networking;

/**
 * Thrown when a packet is invalid
 */
public class InvalidPacketException extends NetworkException {
    public InvalidPacketException(String message) {
        super(message);
    }
}
