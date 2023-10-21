package fr.atlasworld.network.services.database.exceptions.unchecked;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;

import java.util.UUID;

public class NoSuchDatabaseKeyException extends UncheckedDatabaseException {
    public NoSuchDatabaseKeyException(String message, String key) {
        super(message, new DatabaseException("Could not find data with key '" + key + "' in the database."));
    }

    public NoSuchDatabaseKeyException(String message, UUID key) {
        this(message, key.toString());
    }

    public NoSuchDatabaseKeyException(String key) {
        super(new DatabaseException("Could not find data with key '" + key + "' in the database."));
    }

    public NoSuchDatabaseKeyException(UUID key) {
        this(key.toString());
    }
}
