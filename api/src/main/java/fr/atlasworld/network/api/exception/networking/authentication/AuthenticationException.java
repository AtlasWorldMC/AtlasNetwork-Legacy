package fr.atlasworld.network.api.exception.networking.authentication;

import fr.atlasworld.network.api.exception.networking.NetworkException;

/**
 * AuthenticationException, Thrown when something related to authentication fails
 */
public class AuthenticationException extends NetworkException {
    private final String networkFeedback;
    public AuthenticationException(String message, String networkFeedback) {
        super(message);
        this.networkFeedback = networkFeedback;
    }

    public AuthenticationException(String message, String networkFeedback, Throwable cause) {
        super(message, cause);
        this.networkFeedback = networkFeedback;
    }

    public String getNetworkFeedback() {
        return networkFeedback;
    }
}
