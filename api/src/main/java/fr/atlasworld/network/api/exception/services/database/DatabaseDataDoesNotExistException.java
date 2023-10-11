package fr.atlasworld.network.api.exception.services.database;

/**
 * Thrown when data is trying to be processed while it does not exist.
 */
public class DatabaseDataDoesNotExistException extends DatabaseException {
    public DatabaseDataDoesNotExistException() {
        super();
    }

    public DatabaseDataDoesNotExistException(String message) {
        super(message);
    }

    public DatabaseDataDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseDataDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
