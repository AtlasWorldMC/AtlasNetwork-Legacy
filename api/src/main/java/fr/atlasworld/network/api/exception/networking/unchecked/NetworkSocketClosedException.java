package fr.atlasworld.network.api.exception.networking.unchecked;

/**
 * Thrown if code is trying to interact with the socket while it has been closed.
 */
public class NetworkSocketClosedException extends UncheckedNetworkException {
    public NetworkSocketClosedException(String message) {
        super(message);
    }
}
