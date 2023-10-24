package fr.atlasworld.network.services.database.exceptions;

public class DatabaseIOException extends DatabaseException {

    public DatabaseIOException(String message) {
        super(message);
    }

    public DatabaseIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseIOException(Throwable cause) {
        super(cause);
    }
}
