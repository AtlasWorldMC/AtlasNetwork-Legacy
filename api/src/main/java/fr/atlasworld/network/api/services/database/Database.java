package fr.atlasworld.network.api.services.database;

import fr.atlasworld.network.api.exception.services.database.DatabaseException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Database<T> {
    /**
     * Saves a value to the database.
     */
    void save(T value) throws DatabaseException;

    /**
     * Deletes the specified data with the give id.
     * @throws fr.atlasworld.network.api.exception.services.database.DatabaseConnectionClosedException if the give id could not be found.
     */
    void delete(UUID uuid) throws DatabaseException;

    /**
     * Loads data from the database
     * @return null if data with the specified id could not be found.
     */
    @Nullable T load(UUID uuid) throws DatabaseException;

    /**
     * Checks if the database has data for the specified id
     */
    boolean has(UUID uuid) throws DatabaseException;

    /**
     * Retrieve all the values in the database.
     */
    Set<T> getAll() throws DatabaseException;
}
