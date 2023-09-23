package fr.atlasworld.network.database;

import fr.atlasworld.network.database.entities.DatabaseEntity;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Database interface, handles the communication with the database
 * @param <T> Database entity stored in the database
 */
public interface Database<T extends DatabaseEntity> {
    /**
     * Gets an item from the database, if it doesn't exist it returns null
     * @param id item id
     * @return item out of the database, null if it doesn't exist
     * @throws DatabaseException if something went wrong
     */
    @Nullable T get(String id) throws DatabaseException;

    /**
     * Gets an item from the database as an optional, if the item doesn't exist it returns an empty optional
     * @param id item id
     * @return item out of the database, null if it doesn't exist
     * @throws DatabaseException if something went wrong
     */
    Optional<T> getOptional(String id) throws DatabaseException;

    /**
     * Checks if the database has an id
     * @param id item id
     * @return returns true if the item exists
     * @throws DatabaseException if something went wrong
     */
    boolean has(String id) throws DatabaseException;

    /**
     * Saves an item to the database
     * @param value value to save
     * @throws DatabaseException if something went wrong
     */
    void save(T value) throws DatabaseException;

    /**
     * Updates the data of an item in the database
     * @param value new value/data
     * @param id item id
     * @throws DatabaseException if something went wrong
     */
    void update(T value, String id) throws DatabaseException;

    /**
     * Removes an item from the database
     * @param id item id
     * @throws DatabaseException if something went wrong
     */
    void remove(String id) throws DatabaseException;

    /**
     * Retrieves all the items in the database
     * @return all item in the database
     * @throws DatabaseException if something went wrong
     */
    Set<T> getAllEntries() throws DatabaseException;
}
