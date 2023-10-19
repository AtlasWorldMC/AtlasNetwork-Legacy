package fr.atlasworld.network.networking.security.authentication.exceptions;

public class ProfileAlreadyUsedException extends AuthenticationException {
    private static final String FEEDBACK = "PROFILE_USED";

    public ProfileAlreadyUsedException() {
        super(FEEDBACK);
    }

    public ProfileAlreadyUsedException(String message) {
        super(message, FEEDBACK);
    }

    public ProfileAlreadyUsedException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public ProfileAlreadyUsedException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
