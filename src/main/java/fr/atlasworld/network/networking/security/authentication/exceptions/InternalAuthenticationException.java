package fr.atlasworld.network.networking.security.authentication.exceptions;

public class InternalAuthenticationException extends AuthenticationException {
    private static final String FEEDBACK = "INTERNAL_EXCEPTION";

    public InternalAuthenticationException() {
        super(FEEDBACK);
    }

    public InternalAuthenticationException(String message) {
        super(message, FEEDBACK);
    }

    public InternalAuthenticationException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public InternalAuthenticationException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
