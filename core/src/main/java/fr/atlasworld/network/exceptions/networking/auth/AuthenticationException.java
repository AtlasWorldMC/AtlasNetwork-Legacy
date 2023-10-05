package fr.atlasworld.network.exceptions.networking.auth;

/**
 * AuthenticationException, Thrown when something related to authentication fails
 */
public class AuthenticationException extends Exception {
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