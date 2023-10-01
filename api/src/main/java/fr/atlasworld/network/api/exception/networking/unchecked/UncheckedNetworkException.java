package fr.atlasworld.network.api.exception.networking.unchecked;

import fr.atlasworld.network.api.exception.networking.NetworkException;

/**
 * Just like a NetworkException but unchecked.
 * @see NetworkException
 */
public class UncheckedNetworkException extends RuntimeException {
    public UncheckedNetworkException(String message) {
        super(message);
    }

    public UncheckedNetworkException(String message, NetworkException e) {
        super(message, e);
    }

    public UncheckedNetworkException(NetworkException e) {
        super(e);
    }


    public static UncheckedNetworkException of(NetworkException e) {
        return new UncheckedNetworkException(e);
    }
}
