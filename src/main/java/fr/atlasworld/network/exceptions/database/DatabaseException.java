package fr.atlasworld.network.exceptions.database;

import java.io.IOException;

public class DatabaseException extends IOException {
    public DatabaseException(String message) {
        super(message);
    }
}
