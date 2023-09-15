package fr.atlasworld.network.exceptions.networking.session;

import fr.atlasworld.network.exceptions.networking.NetworkException;

/**
 * SessionException, thrown when something related to sessions fails
 */
public class SessionException extends NetworkException {
    public SessionException(String message) {
        super(message);
    }
}
