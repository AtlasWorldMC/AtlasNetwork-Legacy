package fr.atlasworld.network.api.exception.services.database;

import fr.atlasworld.network.api.exception.services.ServiceException;

/**
 * Thrown if something related to the database failed.
 */
public class DatabaseException extends ServiceException {
    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
