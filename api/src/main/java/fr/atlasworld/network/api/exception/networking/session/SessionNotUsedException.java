package fr.atlasworld.network.api.exception.networking.session;

/**
 * Thrown when trying to affect the session while not currently in use
 */
public class SessionNotUsedException extends SessionException {
    public SessionNotUsedException(String message) {
        super(message);
    }
}
