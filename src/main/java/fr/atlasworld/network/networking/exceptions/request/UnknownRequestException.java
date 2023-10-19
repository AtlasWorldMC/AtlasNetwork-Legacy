package fr.atlasworld.network.networking.exceptions.request;

public class UnknownRequestException extends RequestFailureException {
    private static final String FEEDBACK = "UNKNOWN_REQUEST";

    public UnknownRequestException() {
        super(FEEDBACK);
    }

    public UnknownRequestException(String message) {
        super(message, FEEDBACK);
    }

    public UnknownRequestException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public UnknownRequestException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
