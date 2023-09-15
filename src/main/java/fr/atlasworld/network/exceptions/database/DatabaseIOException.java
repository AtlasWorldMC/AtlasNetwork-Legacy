package fr.atlasworld.network.exceptions.database;

/**
 * Thrown when data could not be read or written to the database
 */
public class DatabaseIOException extends DatabaseException {
    public DatabaseIOException(String message) {
        super(message);
    }
}
