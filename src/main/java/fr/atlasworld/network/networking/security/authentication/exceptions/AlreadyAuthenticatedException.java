package fr.atlasworld.network.networking.security.authentication.exceptions;

public class AlreadyAuthenticatedException extends AuthenticationException {
    private static final String FEEDBACK = "ALREADY_AUTHED";

    public AlreadyAuthenticatedException() {
        super(FEEDBACK);
    }

    public AlreadyAuthenticatedException(String message) {
        super(message, FEEDBACK);
    }

    public AlreadyAuthenticatedException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public AlreadyAuthenticatedException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
