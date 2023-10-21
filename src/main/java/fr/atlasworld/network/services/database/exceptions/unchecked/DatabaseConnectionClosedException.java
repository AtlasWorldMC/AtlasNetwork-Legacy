package fr.atlasworld.network.services.database.exceptions.unchecked;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;

public class DatabaseConnectionClosedException extends UncheckedDatabaseException {
    public DatabaseConnectionClosedException(String message) {
        super(message, new DatabaseException("Database connection closed."));
    }

    public DatabaseConnectionClosedException() {
        super(new DatabaseException("Database connection closed."));
    }
}
