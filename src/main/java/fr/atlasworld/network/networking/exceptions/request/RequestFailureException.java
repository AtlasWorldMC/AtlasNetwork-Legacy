package fr.atlasworld.network.networking.exceptions.request;

import fr.atlasworld.network.networking.exceptions.NetworkException;

public class RequestFailureException extends NetworkException {
    private final String networkFeedback;

    public RequestFailureException(String networkFeedback) {
        this.networkFeedback = networkFeedback;
    }

    public RequestFailureException(String message, String networkFeedback) {
        super(message);
        this.networkFeedback = networkFeedback;
    }

    public RequestFailureException(String message, Throwable cause, String networkFeedback) {
        super(message, cause);
        this.networkFeedback = networkFeedback;
    }

    public RequestFailureException(Throwable cause, String networkFeedback) {
        super(cause);
        this.networkFeedback = networkFeedback;
    }

    public String getNetworkFeedback() {
        return networkFeedback;
    }
}
