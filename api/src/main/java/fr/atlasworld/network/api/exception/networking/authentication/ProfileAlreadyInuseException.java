package fr.atlasworld.network.api.exception.networking.authentication;

public class ProfileAlreadyInuseException extends AuthenticationException {
    public ProfileAlreadyInuseException() {
        super("Profile is already inuse!", "PROFILE_ALREADY_INUSE");
    }
}
