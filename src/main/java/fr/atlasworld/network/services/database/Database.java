package fr.atlasworld.network.services.database;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Database<T> {
    /**
     * Saves a value to the database.
     * @throws DatabaseException in case something went wrong while saving.
     */
    void save(T value) throws DatabaseException;

    /**
     * Replaces/Updates data in the database.
     * @throws DatabaseException in case something went wrong while updating data.
     */
    void update(T value) throws DatabaseException;

    /**
     * Deletes the specified data with the give id.
     * @throws DatabaseException in case something went wrong while deleting data.
     * @throws fr.atlasworld.network.services.database.exceptions.unchecked.NoSuchDatabaseKeyException if the give id could not be found.
     */
    void delete(UUID key) throws DatabaseException;

    /**
     * Loads data from the database
     * @throws DatabaseException in case data could not be fetched from the database.
     * @return null if data with the specified id could not be found.
     */
    @Nullable T load(UUID key) throws DatabaseException;

    /**
     * Checks if the database has data for the specified id
     * @throws DatabaseException in case something went wrong while checking for data.
     */
    boolean has(UUID key) throws DatabaseException;

    /**
     * Retrieve all the values in the database.
     * WARNING: This function could be expensive for the network bandwidth and for the database, Use this with caution.
     * @throws DatabaseException in case something went wrong while retrieving entries.
     */
    Set<T> getAll() throws DatabaseException;
}
