package fr.atlasworld.network.networking.security.authentication.exception;

public class AlreadyAuthenticatedException extends AuthenticationException {
    public AlreadyAuthenticatedException(String networkFeedback) {
        super(networkFeedback);
    }

    public AlreadyAuthenticatedException(String message, String networkFeedback) {
        super(message, networkFeedback);
    }

    public AlreadyAuthenticatedException(String message, Throwable cause, String networkFeedback) {
        super(message, cause, networkFeedback);
    }

    public AlreadyAuthenticatedException(Throwable cause, String networkFeedback) {
        super(cause, networkFeedback);
    }
}
