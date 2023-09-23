package fr.atlasworld.network.exceptions.database;

import java.io.IOException;

/**
 * DatabaseException, thrown when something related to the database fails
 */
public class DatabaseException extends IOException {
    public DatabaseException(String message) {
        super(message);
    }
}
