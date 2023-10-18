package fr.atlasworld.network.networking.exceptions.unchecked;

import fr.atlasworld.network.networking.exceptions.NetworkException;

import java.io.UncheckedIOException;

public class UncheckedSocketException extends UncheckedIOException {
    public UncheckedSocketException(String message, NetworkException cause) {
        super(message, cause);
    }

    public UncheckedSocketException(NetworkException cause) {
        super(cause);
    }
}
