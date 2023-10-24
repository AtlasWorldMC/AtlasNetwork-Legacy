package fr.atlasworld.network.services.database.exceptions;

public class DatabaseTimeoutException extends DatabaseException {
    public DatabaseTimeoutException(String message) {
        super(message);
    }

    public DatabaseTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseTimeoutException(Throwable cause) {
        super(cause);
    }
}
