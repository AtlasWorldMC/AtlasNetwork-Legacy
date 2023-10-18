package fr.atlasworld.network.networking.security.authentication.exception;

import fr.atlasworld.network.networking.exceptions.NetworkException;

public class AuthenticationException extends NetworkException {
    private final String networkFeedback;

    public AuthenticationException(String networkFeedback) {
        this.networkFeedback = networkFeedback;
    }

    public AuthenticationException(String message, String networkFeedback) {
        super(message);
        this.networkFeedback = networkFeedback;
    }

    public AuthenticationException(String message, Throwable cause, String networkFeedback) {
        super(message, cause);
        this.networkFeedback = networkFeedback;
    }

    public AuthenticationException(Throwable cause, String networkFeedback) {
        super(cause);
        this.networkFeedback = networkFeedback;
    }
}
