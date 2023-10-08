package fr.atlasworld.network.api.exception.services;

import java.io.IOException;

/**
 * Thrown if something went wrong with one of multiple services.
 * Acts as a root for every sub-services exceptions like {@link fr.atlasworld.network.api.exception.services.database.DatabaseException}
 */
public class ServiceException extends IOException {
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
