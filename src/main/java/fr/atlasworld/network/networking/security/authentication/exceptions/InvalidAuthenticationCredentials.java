package fr.atlasworld.network.networking.security.authentication.exceptions;

public class InvalidAuthenticationCredentials extends AuthenticationException {
    private static final String FEEDBACK = "INVALID_CREDENTIALS";

    public InvalidAuthenticationCredentials() {
        super(FEEDBACK);
    }

    public InvalidAuthenticationCredentials(String message) {
        super(message, FEEDBACK);
    }

    public InvalidAuthenticationCredentials(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public InvalidAuthenticationCredentials(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
