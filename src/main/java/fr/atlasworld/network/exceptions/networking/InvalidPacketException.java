package fr.atlasworld.network.exceptions.networking;

import fr.atlasworld.network.networking.exceptions.NetworkException;

/**
 * Thrown when a packet is invalid
 */
public class InvalidPacketException extends NetworkException {
    public InvalidPacketException(String message) {
        super(message);
    }
}
