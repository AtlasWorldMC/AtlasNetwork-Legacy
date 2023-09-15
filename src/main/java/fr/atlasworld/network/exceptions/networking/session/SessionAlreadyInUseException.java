package fr.atlasworld.network.exceptions.networking.session;

/**
 * Thrown when trying to assign a session to a connection while already used
 */
public class SessionAlreadyInUseException extends SessionException {
    public SessionAlreadyInUseException(String message) {
        super(message);
    }
}
