package fr.atlasworld.network.exceptions.database;

/**
 * Thrown when the database took to long to respond
 */
public class DatabaseTimeoutException extends DatabaseException {
    public DatabaseTimeoutException(String message) {
        super(message);
    }
}
