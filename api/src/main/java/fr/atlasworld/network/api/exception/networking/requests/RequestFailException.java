package fr.atlasworld.network.api.exception.networking.requests;

import fr.atlasworld.network.api.exception.networking.NetworkException;

/**
 * Thrown if a request could not be full-filled.
 */
public class RequestFailException extends NetworkException {
    private final String networkFeedback;

    public RequestFailException(String networkFeedback) {
        this.networkFeedback = networkFeedback;
    }

    public RequestFailException(String message, String networkFeedback) {
        super(message);
        this.networkFeedback = networkFeedback;
    }

    public RequestFailException(String message, Throwable cause, String networkFeedback) {
        super(message, cause);
        this.networkFeedback = networkFeedback;
    }

    public RequestFailException(Throwable cause, String networkFeedback) {
        super(cause);
        this.networkFeedback = networkFeedback;
    }

    public String getNetworkFeedback() {
        return networkFeedback;
    }
}
