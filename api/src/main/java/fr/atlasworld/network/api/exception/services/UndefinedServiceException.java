package fr.atlasworld.network.api.exception.services;

/**
 * Thrown if a service has not been defined and still tried to be accessed.
 */
public class UndefinedServiceException extends ServiceException {
    public UndefinedServiceException() {
        super();
    }

    public UndefinedServiceException(String message) {
        super(message);
    }

    public UndefinedServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedServiceException(Throwable cause) {
        super(cause);
    }
}
