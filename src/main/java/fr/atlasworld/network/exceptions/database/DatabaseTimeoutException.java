package fr.atlasworld.network.exceptions.database;

public class DatabaseTimeoutException extends DatabaseException {
    public DatabaseTimeoutException(String message) {
        super(message);
    }
}
