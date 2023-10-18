package fr.atlasworld.network.networking.exceptions.unchecked;

import fr.atlasworld.network.networking.exceptions.NetworkException;

public class NetworkSocketClosedException extends UncheckedSocketException {
    public NetworkSocketClosedException(String message) {
        super(message, new NetworkException(message));
    }

    public NetworkSocketClosedException(NetworkException e) {
        super(e);
    }
}
