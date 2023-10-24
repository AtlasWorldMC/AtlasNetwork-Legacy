package fr.atlasworld.network.services.database;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import fr.atlasworld.network.services.database.exceptions.unchecked.DatabaseConnectionClosedException;

import java.lang.reflect.Type;

public interface DatabaseService {
    /**
     * Retrieve a database with a name
     * @param name name of the database
     * @param type type of data to parse from the database
     * @return The specified database
     */
    <T extends IDatabaseEntity<T>> Database<T> getDatabase(String name, DatabaseEntityFactory<T> factory) throws DatabaseException;

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
    default void ensureNotClosed() {
        if (this.isClosed()) {
            throw new DatabaseConnectionClosedException();
        }
    }
}
