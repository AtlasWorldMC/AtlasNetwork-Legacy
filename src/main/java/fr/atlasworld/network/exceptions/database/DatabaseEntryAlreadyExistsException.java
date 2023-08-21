package fr.atlasworld.network.exceptions.database;

public class DatabaseEntryAlreadyExistsException extends DatabaseException {
    public DatabaseEntryAlreadyExistsException(String message) {
        super(message);
    }
}
