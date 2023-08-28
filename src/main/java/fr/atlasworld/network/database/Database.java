package fr.atlasworld.network.database;

import fr.atlasworld.network.database.entities.DatabaseEntity;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Database<T extends DatabaseEntity> {
    @Nullable T get(String id) throws DatabaseException;
    boolean has(String id) throws DatabaseException;
    void save(T value) throws DatabaseException;
    void update(T value, String id) throws DatabaseException;
    void remove(String id) throws DatabaseException;
    Set<T> getAllEntries() throws DatabaseException;
}
