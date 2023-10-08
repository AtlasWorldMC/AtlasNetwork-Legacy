package fr.atlasworld.network.api.exception.networking.requests;

public class InternalRequestFailureException extends RequestFailException {
    public InternalRequestFailureException(String message) {
        super(message, "INTERNAL_FAILURE");
    }

    public InternalRequestFailureException(String message, Throwable cause) {
        super(message, cause, "INTERNAL_FAILURE");
    }
}
