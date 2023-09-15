package fr.atlasworld.network.exceptions.database;

/**
 * Thrown when trying to access the database while connection has been closed
 */
public class DatabaseConnectionClosedException extends DatabaseException {
    public DatabaseConnectionClosedException(String message) {
        super(message);
    }
}
