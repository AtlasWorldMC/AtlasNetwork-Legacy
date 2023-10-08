package fr.atlasworld.network.api.exception.networking.session;

import fr.atlasworld.network.api.exception.networking.NetworkException;

/**
 * SessionException, thrown when something related to sessions fails
 */
public class SessionException extends NetworkException {
    public SessionException(String message) {
        super(message);
    }
}
