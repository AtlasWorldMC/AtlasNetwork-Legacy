package fr.atlasworld.network.exceptions.networking.session;

public class SessionAlreadyInUseException extends SessionException {
    public SessionAlreadyInUseException(String message) {
        super(message);
    }
}
