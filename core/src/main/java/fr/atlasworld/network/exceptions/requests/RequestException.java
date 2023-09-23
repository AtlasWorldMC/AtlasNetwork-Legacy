package fr.atlasworld.network.exceptions.requests;

/**
 * RequestException, thrown when something related to web requests fails
 */
public class RequestException extends Exception {
    public RequestException(String message) {
        super(message);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
