package fr.atlasworld.network.api.exception.networking;

/**
 * NetworkException, thrown when something related to the socket/networking part of AtlasNetwork fails
 */
public class NetworkException extends Exception {
    public NetworkException() {
        super();
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }
}
