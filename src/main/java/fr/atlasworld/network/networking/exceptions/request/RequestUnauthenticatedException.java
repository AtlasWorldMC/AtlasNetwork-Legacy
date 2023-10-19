package fr.atlasworld.network.networking.exceptions.request;

public class RequestUnauthenticatedException extends RequestFailureException {
    private static final String FEEDBACK = "NOT_AUTHED";

    public RequestUnauthenticatedException() {
        super(FEEDBACK);
    }

    public RequestUnauthenticatedException(String message) {
        super(message, FEEDBACK);
    }

    public RequestUnauthenticatedException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public RequestUnauthenticatedException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
