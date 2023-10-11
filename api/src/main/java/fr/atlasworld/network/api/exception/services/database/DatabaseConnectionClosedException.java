package fr.atlasworld.network.api.exception.services.database;

/**
 * Thrown if an operation was executed on a closed connection.
 */
public class DatabaseConnectionClosedException extends DatabaseException {
    public DatabaseConnectionClosedException() {
        super();
    }

    public DatabaseConnectionClosedException(String message) {
        super(message);
    }

    public DatabaseConnectionClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseConnectionClosedException(Throwable cause) {
        super(cause);
    }
}
