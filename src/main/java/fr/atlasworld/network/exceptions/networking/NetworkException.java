package fr.atlasworld.network.exceptions.networking;

/**
 * NetworkException, thrown when something related to the socket/networking part of AtlasNetwork fails
 */
public class NetworkException extends Exception {
    public NetworkException(String message) {
        super(message);
    }
}
