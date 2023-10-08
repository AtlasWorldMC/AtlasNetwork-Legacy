package fr.atlasworld.network.api.exception.networking.authentication;

public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException(String reason) {
        super("Authentication failed: " + reason, "AUTH_FAILED");
    }
}
