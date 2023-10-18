package fr.atlasworld.network.networking.exceptions;

import java.io.IOException;

/**
 * NetworkException, thrown when something related to the socket/networking part of AtlasNetwork fails
 */
public class NetworkException extends IOException {
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
