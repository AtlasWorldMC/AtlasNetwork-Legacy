package fr.atlasworld.network.api.services.database;

import fr.atlasworld.network.api.NetworkEntity;
import fr.atlasworld.network.api.exception.services.database.DatabaseConnectionClosedException;
import fr.atlasworld.network.api.exception.services.database.DatabaseException;

import java.lang.reflect.Type;

/**
 * Database service, represents
 */
public interface DatabaseService {
    /**
     * Retrieve a database with a name
     * @param name name of the database
     * @param type type of data to parse from the database
     * @return The specified database
     */
    <T extends NetworkEntity> Database<T> getDatabase(String name, Type type) throws DatabaseException;

    /**
     * Closes the connection with the database
     */
    void closeConnection() throws DatabaseException;

    /**
     * Checks if the database connection is closed.
     */
    boolean isClosed();

    /**
     * Ensures that the connection is not closed.
     * @throws DatabaseConnectionClosedException if the connection is closed
     */
    default void ensureNotClosed() throws DatabaseConnectionClosedException {
        if (this.isClosed()) {
            throw new DatabaseConnectionClosedException("Database connection closed.");
        }
    }
}
