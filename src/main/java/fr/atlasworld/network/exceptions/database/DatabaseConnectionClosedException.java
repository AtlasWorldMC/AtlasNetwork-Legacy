package fr.atlasworld.network.exceptions.database;

public class DatabaseConnectionClosedException extends DatabaseException {
    public DatabaseConnectionClosedException(String message) {
        super(message);
    }
}
