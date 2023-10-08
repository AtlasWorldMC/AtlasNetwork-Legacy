package fr.atlasworld.network.api.exception.networking.authentication;

public class AlreadyAuthenticatedException extends AuthenticationException {
    public AlreadyAuthenticatedException() {
        super("Client is already authenticated!", "ALREADY_AUTHED");
    }
}
