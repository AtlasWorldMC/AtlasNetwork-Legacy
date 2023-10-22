package fr.atlasworld.network.services.database.mongo;

import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class MongoDatabase<T> implements Database<T> {
    @Override
    public void save(T value) throws DatabaseException {

    }

    @Override
    public void delete(UUID key) throws DatabaseException {

    }

    @Override
    public @Nullable T load(UUID key) throws DatabaseException {
        return null;
    }

    @Override
    public boolean has(UUID key) throws DatabaseException {
        return false;
    }

    @Override
    public Set<T> getAll() throws DatabaseException {
        return null;
    }
}
